/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.rm.datasource.undo;

import io.seata.rm.datasource.sql.struct.ColumnMeta;
import io.seata.rm.datasource.sql.struct.Field;
import io.seata.rm.datasource.sql.struct.KeyType;
import io.seata.rm.datasource.sql.struct.Row;
import io.seata.rm.datasource.sql.struct.TableMeta;
import io.seata.rm.datasource.sql.struct.TableRecords;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


/**
 * @author Geng Zhang
 */
public abstract class BaseH2Test {

    static BasicDataSource dataSource = null;

    static Connection connection = null;

    static TableMeta tableMeta = null;

    static TableMeta tableMetaMoreDataType = null;

    @BeforeAll
    public static void start() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:./db_store/test_undo");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        connection = dataSource.getConnection();

        tableMeta = mockTableMeta();
        tableMetaMoreDataType = mockTableMeta4MoreDataType();
    }

    @AfterAll
    public static void stop() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
            }
        }
    }

    @BeforeEach
    private void prepareTable() {
        execSQL("DROP TABLE IF EXISTS table_name");
        execSQL("CREATE TABLE table_name ( `id` int(8), `name` varchar(64), PRIMARY KEY (`id`))");

        execSQL("DROP TABLE IF EXISTS table_order");
        execSQL("CREATE TABLE table_order ( `id` int(8), `name` varchar(64), `count` smallint(5), `price` decimal(16,6), `type` tinyint(3), `creator` bigint(19), `time` datetime, PRIMARY KEY (`id`))");
    }

    protected static void execSQL(String sql) {
        Statement s = null;
        try {
            s = connection.createStatement();
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    protected static TableRecords execQuery(TableMeta tableMeta, String sql) throws SQLException {
        Statement s = null;
        ResultSet set = null;
        try {
            s = connection.createStatement();
            set = s.executeQuery(sql);
            return TableRecords.buildRecords(tableMeta, set);
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (Exception e) {
                }
            }
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    protected static TableMeta mockTableMeta() {
        TableMeta tableMeta = Mockito.mock(TableMeta.class);
        Mockito.when(tableMeta.getPkName()).thenReturn("ID");
        Mockito.when(tableMeta.getTableName()).thenReturn("table_name");
        ColumnMeta meta0 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta0.getDataType()).thenReturn(Types.INTEGER);
        Mockito.when(meta0.getColumnName()).thenReturn("ID");
        Mockito.when(tableMeta.getColumnMeta("ID")).thenReturn(meta0);
        ColumnMeta meta1 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta1.getDataType()).thenReturn(Types.VARCHAR);
        Mockito.when(meta1.getColumnName()).thenReturn("NAME");
        Mockito.when(tableMeta.getColumnMeta("NAME")).thenReturn(meta1);
        return tableMeta;
    }

    protected static Field addField(Row row, String name, int type, Object value) {
        Field field = new Field(name, type, value);
        if (name.equalsIgnoreCase("id")) {
            field.setKeyType(KeyType.PrimaryKey);
        }
        row.add(field);
        return field;
    }

    private static TableMeta mockTableMeta4MoreDataType() {
        TableMeta tableMeta = Mockito.mock(TableMeta.class);
        Mockito.when(tableMeta.getPkName()).thenReturn("ID");
        Mockito.when(tableMeta.getTableName()).thenReturn("table_order");
        ColumnMeta meta0 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta0.getDataType()).thenReturn(Types.INTEGER);
        Mockito.when(meta0.getColumnName()).thenReturn("ID");
        Mockito.when(tableMeta.getColumnMeta("ID")).thenReturn(meta0);
        ColumnMeta meta1 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta1.getDataType()).thenReturn(Types.VARCHAR);
        Mockito.when(meta1.getColumnName()).thenReturn("NAME");
        Mockito.when(tableMeta.getColumnMeta("NAME")).thenReturn(meta1);

        ColumnMeta meta2 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta2.getDataType()).thenReturn(Types.SMALLINT);
        Mockito.when(meta2.getColumnName()).thenReturn("COUNT");
        Mockito.when(tableMeta.getColumnMeta("COUNT")).thenReturn(meta2);

        ColumnMeta meta3 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta3.getDataType()).thenReturn(Types.DECIMAL);
        Mockito.when(meta3.getColumnName()).thenReturn("PRICE");
        Mockito.when(tableMeta.getColumnMeta("PRICE")).thenReturn(meta3);

        ColumnMeta meta4 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta4.getDataType()).thenReturn(Types.TINYINT);
        Mockito.when(meta4.getColumnName()).thenReturn("TYPE");
        Mockito.when(tableMeta.getColumnMeta("TYPE")).thenReturn(meta4);

        ColumnMeta meta5 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta5.getDataType()).thenReturn(Types.BIGINT);
        Mockito.when(meta5.getColumnName()).thenReturn("CREATOR");
        Mockito.when(tableMeta.getColumnMeta("CREATOR")).thenReturn(meta5);

        ColumnMeta meta6 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta6.getDataType()).thenReturn(Types.TIMESTAMP);
        Mockito.when(meta6.getColumnName()).thenReturn("TIME");
        Mockito.when(tableMeta.getColumnMeta("TIME")).thenReturn(meta6);

        return tableMeta;
    }
}
