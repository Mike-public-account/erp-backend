package com.erp.common.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * POI通用Excel工具
 */
public class ExcelUtil {

    /**
     * 导出Excel
     */
    public static <T> void export(List<T> dataList, Class<T> clazz, String fileName, HttpServletResponse response) throws IOException {
        Workbook workbook = null;
        // 后续反射表头、填充数据实现
    }

    /**
     * 导入Excel
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        // 读取文件、数据校验
        return null;
    }
}