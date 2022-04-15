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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by yimi
 * on 2020/9/13
 */
public class ShpFileUtils_Java {


    /**
     * 传入Object集合,导出为shp
     * @param context context
     * @param path 生成shp文件的路径
     * @param fileName 生成shp文件的文件名(.shp结尾,如果不是则自动补充)
     * @param dataList 数据集合,必须有矢量数据字段(默认为points,如果是其他字段,通过pointsName字段指定)
     * @param showAlertDialog 是否弹窗显示错误信息/生成结果等,可通过返回的string处理
     * @param showProgressDialog 未实现  是否显示等待弹窗(如生成过程中显示正在生成数据)
     * @param pointsName 矢量数据字段,传入null或空字符串,则设置为points
     * @param ftypeName 矢量数据类型 0:点 1:线  2:面  传入null或空字符串,则设置为2:面   当前仅实现面数据导出
     * @param excludeList 导出时需排除的字段,不需要排除则传入null或空集合
     * @param format 未实现  导出数据的编码格式,实现后,请修改复制.cpg文件(说明数据编码格式)相关代码  可参考 https://blog.csdn.net/qq_34045114/article/details/84133815
     * @return 结果信息,如 导出成功  path参数异常,导出失败
     */
    public static String createShpByList(Context context,String path,String fileName,
                                         List<Object> dataList,boolean showAlertDialog, boolean showProgressDialog,
                                         String pointsName, String ftypeName, List<String> excludeList, String format, String ftype){
        try {
            if (path == null || path.isEmpty()) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "path参数异常,导出失败");
                }
                return "path参数异常,导出失败";
            }

            File file = new File(path);
            if (!file.exists()) {
                boolean isSuccess = file.mkdirs();
                if (!isSuccess) {
                    if (showAlertDialog) {
                        AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "导出文件夹生成失败,导出失败");
                    }
                    return "导出文件夹生成失败,导出失败";
                }
            }

            if (fileName == null || fileName.isEmpty()) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "文件名为空,导出失败");
                }
                return "文件名为空,导出失败";
            }

            if (dataList == null || dataList.isEmpty()) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "传入数据为空,导出失败");
                }
                return "传入数据为空,导出失败";
            }


            if (!fileName.endsWith(".shp")) {
                fileName += ".shp";
            }

            Class c = dataList.get(0).getClass();

            if (c == null) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "class数据异常,导出失败");
                }
                return "class数据异常,导出失败";
            }


            ogr.RegisterAll();
            gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
            gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");

            String strDriverName = "ESRI Shapefile";
            org.gdal.ogr.Driver oDriver = ogr.GetDriverByName(strDriverName);
            if (oDriver == null) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "驱动不可用,导出失败");
                }
                return "驱动不可用,导出失败";
            }

            Vector<String> options = new Vector<>();
            options.add("ENCODING=UTF-8");
            String shpPath = path + fileName;

            Log.e("yimi", "createShpByList: " + shpPath );

            DataSource oDS = oDriver.CreateDataSource(shpPath, options);
            if (oDS == null) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "创建矢量文件失败,导出失败");
                }
                return "创建矢量文件失败,导出失败";
            }
            oDS.FlushCache();
            org.gdal.osr.SpatialReference srs = new org.gdal.osr.SpatialReference();
            srs.ImportFromWkt("GEOGCS[\"GCS_China_Geodetic_Coordinate_System_2000\",DATUM[\"D_China_2000\",SPHEROID[\"CGCS2000\",6378137.0,298.257222101]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]");

            Layer oLayer;
            if ("0".equals(ftype)) {
                oLayer = oDS.CreateLayer("1", srs, ogr.wkbPoint, null);
            } else if ("1".equals(ftype)) {
                oLayer = oDS.CreateLayer("1", srs, ogr.wkbLineString, null);
            }else {
                oLayer = oDS.CreateLayer("1", srs, ogr.wkbPolygon, null);
            }

                if (oLayer == null) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "图层创建失败,导出失败");
                }
                return "图层创建失败,导出失败";
            }

            if (pointsName == null || pointsName.isEmpty()) {
                pointsName = "Points";
            }
            if (ftypeName == null || ftypeName.isEmpty()) {
                ftypeName = "FType";
            }
            if (excludeList == null) {
                excludeList = new ArrayList<>();
            }


            Field[] fields = c.getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(); //生成到表里面的字段才加到这个集合(getDeclaredFields会获取到构造函数等,需去除)


            //检查是否有points字段
            boolean hasPoints = false;//是否有points字段
            boolean hasFtype = false;//是否有ftype字段(无则按照面处理)
            for (int i = 0; i < fields.length; i++) {
                if (pointsName.equals(fields[i].getName())) {
                    hasPoints = true;
                }
                if (ftypeName.equals(fields[i].getName())) {
                    hasFtype = true;
                }


                //仅导出String int double 等类型(集合里面有些是方法名等不需要的,如 android.os.Parcelable$Creator<com.hzgzsoft.gzgdallib.model.Map_Feature> )
                if (!("class java.lang.String".equals(fields[i].getGenericType().toString())
                        || "class java.lang.Integer".equals(fields[i].getGenericType().toString())
                        || "class java.lang.Double".equals(fields[i].getGenericType().toString())
                        || "int".equals(fields[i].getGenericType().toString())
                        || "double".equals(fields[i].getGenericType().toString())
//                        || "boolean".equals(fields[i].getGenericType().toString())
                )) {
                    continue;
                }

//                if (!(
//                        fields[i].getName().equals("Number")
//                   ||     fields[i].getName().equals("FType")
//                   ||     fields[i].getName().equals("Points")
//                   ||     fields[i].getName().equals("Remark")
//                )) {
//                    continue;
//                }

                //points字段可能超长
                if (fields[i].getName().equals("Points")
                        || fields[i].getName().equals(pointsName)

//                        || fields[i].getName().equals("Area")
//                        || fields[i].getName().equals("Ftype")
                ) {
                    continue;
                }

                if (!excludeList.contains(fields[i].getName())) {
                    //不在排除集合的属性,才加入集合
                    fieldList.add(fields[i]);
                }
            }


            if (!hasPoints) {
                if (showAlertDialog) {
                    AlertDialogUtil.INSTANCE.showAlertDialog(context, "提示", "传入数据无points字段,导出失败");
                }
                return "传入数据无points字段,导出失败";
            }


            //创建属性表
            for (int i = 0; i < fieldList.size(); i++) {
//                Log.e("yimi", "createShpFile_属性名: " + fieldList.get(i).getName());
//                Log.e("yimi", "createShpFile_类型: " + fieldList.get(i).getGenericType());

                if ("class java.lang.String".equals(fieldList.get(i).getGenericType().toString())) {
                    Log.i("yimi", "createShpFile_String: " + fieldList.get(i).getName());
                    FieldDefn oFieldName = new FieldDefn(new String(fieldList.get(i).getName().getBytes(), StandardCharsets.UTF_8), ogr.OFTString);
                    oFieldName.SetWidth(254);
                    oFieldName.SetNullable(1);
                    oLayer.CreateField(oFieldName);

                } else if ("class java.lang.Integer".equals(fieldList.get(i).getGenericType().toString()) || "int".equals(fieldList.get(i).getGenericType().toString())) {
                    Log.i("yimi", "createShpFile_int: " + fieldList.get(i).getName());
                    FieldDefn oFieldName = new FieldDefn(new String(fieldList.get(i).getName().getBytes(), StandardCharsets.UTF_8), ogr.OFTInteger);
                    oFieldName.SetNullable(1);
                    oLayer.CreateField(oFieldName);

                } else if ("class java.lang.Double".equals(fieldList.get(i).getGenericType().toString()) || "double".equals(fieldList.get(i).getGenericType().toString())) {
                    Log.i("yimi", "createShpFile_double: " + fieldList.get(i).getName());
                    FieldDefn oFieldName = new FieldDefn(new String(fieldList.get(i).getName().getBytes(), StandardCharsets.UTF_8), ogr.OFTReal);
                    oFieldName.SetNullable(1);
                    oLayer.CreateField(oFieldName);

                }else if ("class java.lang.Boolean".equals(fieldList.get(i).getGenericType().toString()) || "boolean".equals(fieldList.get(i).getGenericType().toString())) {
                    Log.i("yimi", "createShpFile_boolean: " + fieldList.get(i).getName());
                    FieldDefn oFieldName = new FieldDefn(new String(fieldList.get(i).getName().getBytes(), StandardCharsets.UTF_8), ogr.OFSTBoolean);
                    oFieldName.SetNullable(1);
                    oLayer.CreateField(oFieldName);
                }
            }


            FeatureDefn oDefn = oLayer.GetLayerDefn();
            //写入数据
            for (int i = 0; i < dataList.size(); i++) {
                Feature feature = new Feature(oDefn);
                for (int j = 0; j < fieldList.size(); j++) {
//                    feature.SetField(0, j);
//                    feature.SetField(1, new String(dataList.get(i).getRemark().getBytes(), StandardCharsets.UTF_8));
//                    feature.SetField(2, new String(dataList.get(i).getNumber().getBytes(), StandardCharsets.UTF_8));

                    if ("class java.lang.String".equals(fieldList.get(j).getGenericType().toString())) {
                        Method m = c.getMethod("get" + upperCase(fieldList.get(j).getName()));
                        String val = (String) m.invoke(dataList.get(i));// 调用getter方法获取属性值
                        if (val != null) {

                            //这两个方法需完善,当前不能传入空字符串,空字符串使用  feature.SetField(j,val);
//                            feature.SetFieldBinaryFromHexString(j, str2HexStr(val, "UTF-8"));
//                            feature.SetFieldBinaryFromHexString(j, str2HexStr(val, "GBK"));
                            feature.SetField(j,val);
                        } else {
                            feature.SetFieldNull(j);
                        }
                    } else if ("class java.lang.Integer".equals(fieldList.get(j).getGenericType().toString()) || "int".equals(fieldList.get(j).getGenericType().toString())) {
                        Method m = c.getMethod("get" + upperCase(fieldList.get(j).getName()));
                        Integer val = (Integer) m.invoke(dataList.get(i));   //调用getter方法获取属性值
                        if (val != null) {
                            feature.SetField(j,val);
                        } else {
                            feature.SetFieldNull(j);
                        }
                    }else if ("class java.lang.Double".equals(fieldList.get(j).getGenericType().toString()) || "double".equals(fieldList.get(j).getGenericType().toString())) {
                        Method m = c.getMethod("get" + upperCase(fieldList.get(j).getName()));
                        Double val = (Double) m.invoke(dataList.get(i));   //调用getter方法获取属性值
                        if (val != null) {
                            feature.SetField(j,val);
                        } else {
                            feature.SetFieldNull(j);
                        }
                    }
                }


//                Log.e("yimi", "createShpByList_pointsName: "+ pointsName );
                Method m = c.getMethod("get" + upperCase(pointsName));
                String val = (String) m.invoke(dataList.get(i));// 调用getter方法获取属性值
                Geometry geometry = Geometry.CreateFromWkt(pointsToWktString(ftype,val));
                feature.SetGeometry(geometry);
                oLayer.CreateFeature(feature);

            }


            oLayer.SyncToDisk();
            oDS.SyncToDisk();
            oDS.delete();


//            for (int i = 0; i < fields.length; i++) {
//                Log.e("yimi", "createShpFile_属性名: " + fields[i].getName());
//                Log.e("yimi", "createShpFile_类型: " + fields[i].getGenericType());
//
//                if ("class java.lang.String".equals(fields[i].getGenericType().toString())) {
//                    Log.e("yimi", "createShpFile_字符类型: ");
//                    try {
//                        Method m = c.getMethod("get" + upperCase(fields[i].getName()));
//
//                        String val = (String) m.invoke(dataList.get(0));// 调用getter方法获取属性值
//                        if (val != null) {
//                            Log.i("yimi", "createShpFile: " + val);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

            //替换.prj(代码生成的在平台导入时会提示不支持自定义坐标系)  放入.cpg(说明编码格式为utf-8)
            replaceFiles(context,path,fileName.replace(".shp",""));


            if (showAlertDialog) {
                AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出成功", "导出路径: " + shpPath);
            }
            return "导出成功";
        }catch (Exception e) {
            e.printStackTrace();
            if (showAlertDialog) {
                AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出失败", "发生异常: " + e.getMessage());
            }
            return "发生异常: " + e.getMessage();
        }
    }

    /**
     * 替换.prj(代码生成的在平台导入时会提示不支持自定义坐标系)  放入.cpg(说明编码格式为utf-8)
     * @return
     */
    private static boolean replaceFiles (Context context,String path,String name){
        try {
            InputStream isCPG = context.getResources().getAssets().open("ZZY.cpg");//欲导入的数据库
            FileOutputStream fosCPG = new FileOutputStream(path + name + ".cpg");
            byte[] bufferCPG = new byte[1024 * 10];
            int countCPG;
            while ((countCPG = isCPG.read(bufferCPG)) > 0) {
                fosCPG.write(bufferCPG, 0, countCPG);
            }
            fosCPG.close();
            isCPG.close();

            InputStream isPRJ = context.getResources().getAssets().open("ZZY.prj");//欲导入的数据库
            FileOutputStream fosPRJ = new FileOutputStream(path + name + ".prj");
            byte[] bufferPRJ = new byte[1024 * 10];
            int count;
            while ((count = isPRJ.read(bufferPRJ)) > 0) {
                fosPRJ.write(bufferPRJ, 0, count);
            }
            fosPRJ.close();
            isPRJ.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


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

                // Int 测试
                FieldDefn oFieldIyear = new FieldDefn("Iyear", ogr.OFTInteger);
                oLayer.CreateField(oFieldIyear);

                // double 测试
                FieldDefn oFieldArea = new FieldDefn("Area", ogr.OFTReal);
                oLayer.CreateField(oFieldArea);


                int index = 0;

                FeatureDefn oDefn = oLayer.GetLayerDefn();
                for (int i = 0; i < dataList.size(); i++) {
                    if (!"2".equals(dataList.get(i).getFType())) {
                        continue;
                    }
                    index++;
                    Feature oFeatureTriangle = new Feature(oDefn);
                    oFeatureTriangle.SetField(0, index);

                    //中文处理
                    oFeatureTriangle.SetField(1, str2HexStr(dataList.get(i).getRemark(), "GBK"));
//                    oFeatureTriangle.SetField(1, new String(dataList.get(i).getRemark().getBytes(), StandardCharsets.UTF_8));
                    oFeatureTriangle.SetField(2, new String(dataList.get(i).getNumber().getBytes(), StandardCharsets.UTF_8));

                    oFeatureTriangle.SetField(3, dataList.get(i).getIYear());

                    oFeatureTriangle.SetField(4, dataList.get(i).getArea());

                    Geometry geomTriangle = Geometry.CreateFromWkt(pointsToWktString(dataList.get(i).getFType(), dataList.get(i).getPoints()));

                    oFeatureTriangle.SetGeometry(geomTriangle);
                    oLayer.CreateFeature(oFeatureTriangle);

                }

                oLayer.SyncToDisk();
                oDS.SyncToDisk();
                oDS.delete();



                AlertDialogUtil.INSTANCE.showAlertDialog(context, "导出成功", "位置: " + shpPath);

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
//                Log.e("yimi", "pointsToWktString(点): " + result);
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
//                Log.e("yimi", "pointsToWktString(线): " + result);
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


    //https://blog.csdn.net/qq_34045114/article/details/84133815
    public static String str2HexStr(String origin,String charsetName) throws UnsupportedEncodingException {

//        if (origin == null) {
//            Log.e("yimi", "str2HexStr: origin == null");
//        }
//        if ("null".equals(origin)) {
//            Log.e("yimi", "str2HexStr: origin == null 222222");
//        }

//        Log.e("yimi", "str2HexStr_origin: "+ origin + "  charsetName: " + charsetName);

        byte[] bytes = origin.getBytes(charsetName);
        String hex = bytesToHexString(bytes);

//        Log.e("yimi", "str2HexStr_result: "+ hex );
//
//        if (hex == null || hex.isEmpty()) {
//            Log.e("yimi", "str2HexStr: reeult is null" );
//            return "";
//        }
//        if ("null".equals(hex)) {
//            Log.e("yimi", "str2HexStr: reeult is null 222222222" );
//            return "";
//        }

        return hex;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static void test(){

        List<Object> dataList = new ArrayList<>();
        Map_Feature feature = new Map_Feature();
        feature.setCodeId("333");
        feature.setVillageName("44444");

        feature.setPoints("119.38632499,28.15385465;119.38493879,28.15417115;119.38596560,28.15578788;119.38766841,28.15509499;119.38766841,28.15387175;119.38632499,28.15385465;#119.38649613,28.15417115;119.38691541,28.15434223;119.38714644,28.15482126;119.38653035,28.15509499;119.38579447,28.15454753;119.38649613,28.15417115;#119.38882601,28.15428938;119.38838533,28.15456311;119.38886879,28.15535009;119.38964746,28.15509774;119.38935225,28.15429793;119.38882601,28.15428938;#119.38848986,28.15534306;119.38739459,28.15601884;119.38831872,28.15707100;119.38899471,28.15658341;119.38890914,28.15564246;119.38848986,28.15534306;#119.38844708,28.15576222;119.38869522,28.15612149;119.38867811,28.15652353;119.38819037,28.15642088;119.38802779,28.15593330;119.38844708,28.15576222;");
        feature.setIYear(2019);
        feature.setFType("2");
        dataList.add(feature);

        createShpFile(Map_Feature.class,dataList);


    }



    public static void createShpFile(Class c,List<Object> dataList) {

        Field[] fields = c.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Log.e("yimi", "createShpFile_属性名: " + fields[i].getName());
            Log.e("yimi", "createShpFile_类型: " + fields[i].getGenericType());

            if ("class java.lang.String".equals(fields[i].getGenericType().toString())) {
                Log.e("yimi", "createShpFile_字符类型: ");
                try {
                    Method m = c.getMethod("get" + upperCase(fields[i].getName()));

                    String val = (String) m.invoke(dataList.get(0));// 调用getter方法获取属性值
                    if (val != null) {
                        Log.i("yimi", "createShpFile: " + val);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }



    // 把一个字符串的第一个字母大写、效率是最高的、
  private static String getMethodName(String fildeName) throws Exception {
      byte[] items = fildeName.getBytes();
      items[0] = (byte) ((char) items[0] - 'a' + 'A');
      return new String(items);
  }

    /**
     * 把一个字符串的第一个字母大写
     * @param str
     * @return
     */
    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }


    /**
     * 类似 Double.parseDouble 方法,但处理了 null,空字符串等情况
     * 说明:null/字符串/非数值等按照 0 返回
     *
     * @return
     */
    public static int parseInt(String value) {
        if (value == null || value.isEmpty() || !isNumber_int(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    /**
     * 正则判断是否是数值(double或 int)
     *
     * @param str
     * @return
     */
    public static boolean isNumber_int_double(String str) {
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        //可以判断正负、整数小数

        boolean isInt = Pattern.compile("^-?[0-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([0-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
        return isInt || isDouble;
    }

    /**
     * 正则判断是否是数值(仅int)
     *
     * @param str
     * @return
     */
    public static boolean isNumber_int(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        return Pattern.compile("^-?[0-9]\\d*$").matcher(str).find();
    }



}
