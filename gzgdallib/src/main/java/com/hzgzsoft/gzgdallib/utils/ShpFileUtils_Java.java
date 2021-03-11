package com.hzgzsoft.gzgdallib.utils;


import android.content.Context;
import android.util.Log;

import com.hzgzsoft.gzgdallib.model.Map_Feature;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

/**
 * Created by yimi
 * on 2020/9/13
 */
public class ShpFileUtils_Java {


    /**
     * 按照second值生成shp文件(仅面数据(Ftype = 2))
     *
     * @return
     */
    public static boolean createShp_BySecond_Polygon(Context context, String path, String shpFileName, List<Map_Feature> dataList) {
        try {
            if (shpFileName == null || shpFileName.isEmpty()) {
                AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "参数异常(fileName),不能导出shp");
                return false;
            }

            if (dataList == null || dataList.isEmpty()) {
                AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "当前编辑图层无数据,不能导出shp");
                return false;
            }

            {
                File projectFolder = new File(path);
                if (!projectFolder.exists()) {
                    projectFolder.mkdir();
                }
                if (!shpFileName.endsWith(".shp")) {
                    shpFileName += ".shp";
                }
                String shpPath = path + shpFileName;


                ogr.RegisterAll();
                gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
                gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");

                String strDriverName = "ESRI Shapefile";
                org.gdal.ogr.Driver oDriver = ogr.GetDriverByName(strDriverName);
                if (oDriver == null) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出失败", "驱动不可用");
                    return false;
                }

                Vector<String> options = new Vector<>();
                options.add("ENCODING=UTF-8");
                DataSource oDS = oDriver.CreateDataSource(shpPath, options);
                if (oDS == null) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出失败", "创建矢量文件失败");
                    return false;
                }
                oDS.FlushCache();
                org.gdal.osr.SpatialReference srs = new org.gdal.osr.SpatialReference();
                srs.ImportFromWkt("GEOGCS[\"GCS_China_Geodetic_Coordinate_System_2000\",DATUM[\"D_China_2000\",SPHEROID[\"CGCS_2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"DEGREE\",0.017453292519943295]]");
                Layer oLayer = oDS.CreateLayer("1", srs, ogr.wkbPolygon, null);
                if (oLayer == null) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出失败", "图层创建失败");
                    return false;
                }

                // 下面创建属性表
                // 先创建一个叫FieldID的整型属性
                FieldDefn oFieldID = new FieldDefn("Index", ogr.OFTInteger);
                oLayer.CreateField(oFieldID);

                // 再创建一个叫FeatureName的字符型属性，字符长度为50
                FieldDefn oFieldName = new FieldDefn(new String("Remark".getBytes(), StandardCharsets.UTF_8), ogr.OFTString);
                oFieldName.SetWidth(100);
                oLayer.CreateField(oFieldName);

                // 再创建一个叫FeatureName的字符型属性，字符长度为50
                FieldDefn oFieldName2 = new FieldDefn(new String("MobileNo".getBytes(), StandardCharsets.UTF_8), ogr.OFTString);
                oFieldName2.SetWidth(100);
                oLayer.CreateField(oFieldName2);


                int index = 0;

                FeatureDefn oDefn = oLayer.GetLayerDefn();
                for (int i = 0; i < dataList.size(); i++) {
                    if (!"2".equals(dataList.get(i).getFType())) {
                        continue;
                    }
                    index++;
                    Feature oFeatureTriangle = new Feature(oDefn);
                    oFeatureTriangle.SetField(0, index);
                    oFeatureTriangle.SetField(1, new String(dataList.get(i).getRemark().getBytes(), StandardCharsets.UTF_8));
                    oFeatureTriangle.SetField(2, new String(dataList.get(i).getNumber().getBytes(), StandardCharsets.UTF_8));


                    Geometry geomTriangle = Geometry.CreateFromWkt(pointsToWktString(dataList.get(i).getFType(), dataList.get(i).getPoints()));

                    oFeatureTriangle.SetGeometry(geomTriangle);
                    oLayer.CreateFeature(oFeatureTriangle);

                }

                oLayer.SyncToDisk();
                oDS.SyncToDisk();
                oDS.delete();

                Log.e("yimi", "createShp: 生成成功");

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * points字段转换为wkt格式字符串(暂仅面类型数据)
     *
     * @param lType 类型 (0:点/1:线/2:面) 与map_feature的 FType对应  传其他值按照面处理
     * @return
     */
    private static String pointsToWktString(String lType, String points) {
        try {
            if ("0".equals(lType)) {
                //点
                if (points == null || points.isEmpty()) {
                    return "";
                }
                String result = "";
//                POINT (30 10)
//                MULTIPOINT ((10 40), (40 30), (20 20), (30 10))
                if (points.contains("#")) {
                    result += "MULTIPOINT (";
                } else {
                    result += "POINT ";
                }
                String[] data = points.split("#");

                for (int i = 0; i < data.length; i++) {
                    if (data[i].isEmpty()) {
                        continue;
                    }
                    result += "(";
                    String[] data2 = data[i].split(";");
                    for (int j = 0; j < data2.length; j++) {
                        if (data2[j].isEmpty()) {
                            continue;
                        }
                        String[] point = data2[j].split(",");
                        if (point.length != 2) {
                            continue;
                        }
                        result += point[0] + " " + point[1] + ",";
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    result += "),";
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                if (points.contains("#")) {
                    result += ")";
                } else {
                    result += "";
                }
                Log.e("yimi", "pointsToWktString(点): " + result);
                return result;
            } else if ("1".equals(lType)) {
                //线
                if (points == null || points.isEmpty()) {
                    return "";
                }
                String result = "";
//                LINESTRING (30 10, 10 30, 40 40)
//                MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))
                if (points.contains("#")) {
                    result += "MULTILINESTRING (";
                } else {
                    result += "LINESTRING ";
                }
                String[] data = points.split("#");

                for (int i = 0; i < data.length; i++) {
                    if (data[i].isEmpty()) {
                        continue;
                    }
                    result += "(";
                    String[] data2 = data[i].split(";");
                    for (int j = 0; j < data2.length; j++) {
                        if (data2[j].isEmpty()) {
                            continue;
                        }
                        String[] point = data2[j].split(",");
                        if (point.length != 2) {
                            continue;
                        }
                        result += point[0] + " " + point[1] + ",";
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    result += "),";
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                if (points.contains("#")) {
                    result += ")";
                } else {
                    result += "";
                }
                Log.e("yimi", "pointsToWktString(线): " + result);
                return result;


            } else {
                //面
                if (points == null || points.isEmpty()) {
                    return "";
                }
                String result = "";
//            'polygon ((10 10, 10 20, 20 20, 20 15, 10 10))'  ==    10,10;20,20
//            'multipolygon (((10 10, 10 20, 20 20, 20 15 , 10 10), (50 40, 50 50, 60 50, 60 40, 50 40)))' 10,10;20,20#10,10;20,20
                if (points.contains("#")) {
                    result += "multipolygon ((";
                } else {
                    result += "polygon (";
                }
                String[] data = points.split("#");

                for (int i = 0; i < data.length; i++) {
                    if (data[i].isEmpty()) {
                        continue;
                    }
                    result += "(";
                    String[] data2 = data[i].split(";");
                    for (int j = 0; j < data2.length; j++) {
                        if (data2[j].isEmpty()) {
                            continue;
                        }
                        String[] point = data2[j].split(",");
                        if (point.length != 2) {
                            continue;
                        }
                        result += point[0] + " " + point[1] + ",";
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    result += "),";
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                if (points.contains("#")) {
                    result += "))";
                } else {
                    result += ")";
                }
                Log.e("yimi", "pointsToWktString(面): " + result);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


}
