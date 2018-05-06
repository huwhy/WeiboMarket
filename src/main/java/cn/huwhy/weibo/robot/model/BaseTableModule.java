package cn.huwhy.weibo.robot.model;

/**
 * 说明:自定义TabelModel工具类
 */

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BaseTableModule extends AbstractTableModel {

    List<List<String>> rows;// 定义行
    List<String> columns;// 定义列

    public BaseTableModule(String[] params, List<List<String>> lists) {
        // 初始化列
        this.columns = new ArrayList<>();
        for (String col : params) {
            columns.add(col);
        }

        // 初始化行
        this.rows = lists;

    }

    @Override
    public String getColumnName(int column) {
        return this.columns.get(column);
    }

    @Override
    public int getColumnCount() {
        return this.columns.size();
    }

    @Override
    public int getRowCount() {
        return this.rows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).get(columnIndex);
    }

}
