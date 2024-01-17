package cn.nexura.nextbi.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONUtil;
import cn.nexura.nextbi.common.ErrorCode;
import cn.nexura.nextbi.constant.CommonConstant;
import cn.nexura.nextbi.exception.BusinessException;
import cn.nexura.nextbi.exception.ThrowUtils;
import cn.nexura.nextbi.manager.AiManager;
import cn.nexura.nextbi.mapper.ChartMapper;
import cn.nexura.nextbi.model.dto.chart.ChartQueryRequest;
import cn.nexura.nextbi.model.entity.Chart;
import cn.nexura.nextbi.model.entity.User;
import cn.nexura.nextbi.model.vo.BiResponse;
import cn.nexura.nextbi.mq.BiMessageProducer;
import cn.nexura.nextbi.service.ChartService;
import cn.nexura.nextbi.utils.ExcelUtils;
import cn.nexura.nextbi.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
* @author 86188
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-01-10 16:33:02
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {


    @Resource
    private AiManager aiManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BiMessageProducer messageProducer;



    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = chartQueryRequest.getId();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        String name = chartQueryRequest.getName();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chart_type", chartType);
        queryWrapper.eq(userId != null && userId > 0, "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public BiResponse doGenChart(MultipartFile multipartFile, User loginUser, String goal, String name, String chartType) {
        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("Analysis goal:").append("\n");

        String userGoal = goal;
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(chartType)) {
            userGoal += ",请使用" + chartType;
        }

        userInput.append(userGoal).append("\n");

        // 获得数据
        String data = ExcelUtils.excelToCsv(multipartFile);


        userInput.append("Row data:").append("\n").append(data).append("\n");


        long biModelId = 1709156902984093697L;

        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splitRes = result.split("【【【【【");
        if (splitRes.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成错误");
        }


        String genChart = splitRes[1].trim();
        String genResult = splitRes[2].trim();

        // 保存信息
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(userGoal);
        chart.setChartData(data);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        boolean save = this.save(chart);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "信息保存失败");

        sharding(data, chart.getId());

        return BiResponse.builder()
                .genResult(genResult)
                .genChart(genChart)
                .chartId(chart.getId())
                .chartData(getChartData(chart))
                .build();
    }

    @Override
    public String getChartData(Chart chart) {
        // 根据id查询数据库
        List<Map<String, String>> chartData = baseMapper.getChartDataByChartId(chart.getId());

        List<Collection<String>> excelData = new ArrayList<>();

        for (int i = 0; i < chartData.size(); i++) {
            Map<String, String> chartDatum = chartData.get(i);
            if (i == 0) {
                Set<String> keys = chartDatum.keySet();
                excelData.add(keys);
            } else {
                Collection<String> values = chartDatum.values();
                excelData.add(values);
            }
        }

        return JSONUtil.toJsonStr(excelData);
    }

    @Override
    public BiResponse doGenChartAsync(MultipartFile multipartFile, User loginUser, String goal, String name, String chartType) {
        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("Analysis goal:").append("\n");

        String userGoal = goal;
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(chartType)) {
            userGoal += ",请使用" + chartType;
        }

        userInput.append(userGoal).append("\n");

        // 获得数据
        String data = ExcelUtils.excelToCsv(multipartFile);


        userInput.append("Row data:").append("\n").append(data).append("\n");

        // 保存信息
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(userGoal);
        chart.setChartData(data);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean save = this.save(chart);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "信息保存失败");

        sharding(data, chart.getId());


        messageProducer.sendMessage(chart.getId().toString());

        return BiResponse.builder()
                .chartId(chart.getId())
                .build();
    }


    private void sharding(String data, long chartId) {

        // 根据\n分割数据，拆分为表头和数据
        String[] rows = data.split("\n");
        String[] tableHeaders = rows[0].split(",");
        List<String[]> tableDataList = new ArrayList<>();
        List<String> tableField = new ArrayList<>(Arrays.asList(tableHeaders));

        tableDataList.add(tableField.toArray(new String[0]));
        for (int i = 1; i < rows.length; i++) {
            String[] tableData = rows[i].split(",");
            tableDataList.add(tableData);
        }

        // 根据表头进行建表操作
        boolean b = baseMapper.createTable(tableField, chartId);

        // 根据数据，对数据表插入数据
        boolean b1 = baseMapper.insertDataBatch(tableDataList, chartId);
        ThrowUtils.throwIf(!b1, ErrorCode.PARAMS_ERROR, "数据有误");

    }

}




