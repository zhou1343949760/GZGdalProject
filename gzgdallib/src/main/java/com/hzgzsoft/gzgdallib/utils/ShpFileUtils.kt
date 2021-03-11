package com.hzgzsoft.gzgdallib.utils

import android.content.Context
import android.util.Log
import com.hzgzsoft.gzgdallib.model.Map_Feature
import org.gdal.gdal.gdal
import org.gdal.ogr.Feature
import org.gdal.ogr.FieldDefn
import org.gdal.ogr.Geometry
import org.gdal.ogr.ogr
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 由java代码转换并修改,对应代码在 ShpFileUtils_Java
 * Created by yimi
 * on 2021/3/10
 */
object ShpFileUtils {


    /**
     * 生成shp文件(仅面数据(Ftype = 2))
     * @param context 必须是Activity的context,主要用于生成AlertDialog
     * @param filePath 生成shp文件的文件夹,无则创建  未适配存储权限,如失败,请设置为 android -> data -> 包名 文件夹
     * @param shpFileName 生成的文件的文件名
     * @return 出错返回false
     */
    fun createShp_BySecond_Polygon(
        context: Context,
        filePath: String?,
        shpFileName: String?,
        dataList:ArrayList<Map_Feature>?
    ): Boolean {
        var shpFileName = shpFileName
        try {
            if (filePath == null || filePath.isEmpty()) {
                AlertDialogUtil.showAlertDialog(context, "提示", "参数异常(filePath),不能导出shp")
                return false
            }
            if (shpFileName == null || shpFileName.isEmpty()) {
                AlertDialogUtil.showAlertDialog(context, "提示", "参数异常(fileName),不能导出shp")
                return false
            }

            if (dataList == null || dataList.isEmpty()) {
                AlertDialogUtil.showAlertDialog(context, "提示", "当传入数据为空,不能导出shp")
                return false
            }

            run {
                val projectFolder = File(filePath)
                if (!projectFolder.exists()) {
                    projectFolder.mkdir()
                }
                if (!shpFileName.endsWith(".shp")) {
                    shpFileName += ".shp"
                }
                val shpPath = filePath + shpFileName


                ogr.RegisterAll()
                gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES")
                gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8")

                val strDriverName = "ESRI Shapefile"
                val oDriver = ogr.GetDriverByName(strDriverName)
                if (oDriver == null) {
                    AlertDialogUtil.showAlertDialog(context, "导出失败", "驱动不可用")
                    return false
                }

                val options = Vector<String>()
                options.add("ENCODING=UTF-8")
                val oDS = oDriver.CreateDataSource(shpPath, options)
                if (oDS == null) {
                    AlertDialogUtil.showAlertDialog(context, "导出失败", "创建矢量文件失败")
                    return false
                }
                oDS.FlushCache()
                val srs = org.gdal.osr.SpatialReference()
                srs.ImportFromWkt("GEOGCS[\"GCS_China_Geodetic_Coordinate_System_2000\",DATUM[\"D_China_2000\",SPHEROID[\"CGCS_2000\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"DEGREE\",0.017453292519943295]]")
                val oLayer = oDS.CreateLayer("1", srs, ogr.wkbPolygon, null)
                if (oLayer == null) {
                    AlertDialogUtil.showAlertDialog(context, "导出失败", "图层创建失败")
                    return false
                }

                // 下面创建属性表
                // 先创建一个叫FieldID的整型属性
                val oFieldID = FieldDefn("Index", ogr.OFTInteger)
                oLayer.CreateField(oFieldID)

                // 再创建一个叫FeatureName的字符型属性，字符长度为50
                val oFieldName = FieldDefn(String("Remark".toByteArray(), StandardCharsets.UTF_8), ogr.OFTString)
//                val oFieldName = FieldDefn(ShpFileUtils_Java.str2HexStr("备注","GBK"), ogr.OFTString)
                oFieldName.SetWidth(100)
                oLayer.CreateField(oFieldName)

                // 再创建一个叫FeatureName的字符型属性，字符长度为50
                val oFieldName2 =
                    FieldDefn(
                        String("MobileNo".toByteArray(), StandardCharsets.UTF_8),
                        ogr.OFTString
                    )
                oFieldName2.SetWidth(100)
                oLayer.CreateField(oFieldName2)


                var index = 0

                val oDefn = oLayer.GetLayerDefn()
                for (i in dataList.indices) {
                    if ("2" != dataList[i].fType) {
                        continue
                    }
                    index++
                    val oFeatureTriangle = Feature(oDefn)
                    oFeatureTriangle.SetField(0, index)
                    oFeatureTriangle.SetFieldBinaryFromHexString(
                        1,
//                        String(dataList[i].remark.toByteArray(), StandardCharsets.UTF_8)
                        ShpFileUtils_Java.str2HexStr(dataList[i].remark,"GBK")
                    )
                    oFeatureTriangle.SetField(
                        2,
                        String(dataList[i].number.toByteArray(), StandardCharsets.UTF_8)
                    )


                    val geomTriangle = Geometry.CreateFromWkt(
                        pointsToWktString(
                            dataList[i].fType,
                            dataList[i].points
                        )
                    )

                    oFeatureTriangle.SetGeometry(geomTriangle)
                    oLayer.CreateFeature(oFeatureTriangle)

                }

                oLayer.SyncToDisk()
                oDS.SyncToDisk()
                oDS.delete()

                AlertDialogUtil.showAlertDialog(context,"生成成功", "文件路径:$filePath$shpFileName")
                Log.e("yimi", "createShp: 生成成功")

            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    /**
     * points字段转换为wkt格式字符串(暂仅面类型数据)
     * @param lType 类型 (0:点/1:线/2:面) 与map_feature的 FType对应  传其他值按照面处理
     * @return
     */
    fun pointsToWktString(lType: String, points: String?): String {
        try {
            if ("0" == lType) {
                //点
                if (points == null || points.isEmpty()) {
                    return ""
                }
                var result = ""
                //                POINT (30 10)
                //                MULTIPOINT ((10 40), (40 30), (20 20), (30 10))
                if (points.contains("#")) {
                    result += "MULTIPOINT ("
                } else {
                    result += "POINT "
                }
                val data = points.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for (i in data.indices) {
                    if (data[i].isEmpty()) {
                        continue
                    }
                    result += "("
                    val data2 =
                        data[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (j in data2.indices) {
                        if (data2[j].isEmpty()) {
                            continue
                        }
                        val point = data2[j].split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (point.size != 2) {
                            continue
                        }
                        result += point[0] + " " + point[1] + ","
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length - 1)
                    }
                    result += "),"
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length - 1)
                }
                if (points.contains("#")) {
                    result += ")"
                } else {
                    result += ""
                }
//                Log.e("yimi", "pointsToWktString(点): $result")
                return result
            } else if ("1" == lType) {
                //线
                if (points == null || points.isEmpty()) {
                    return ""
                }
                var result = ""
                //                LINESTRING (30 10, 10 30, 40 40)
                //                MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))
                if (points.contains("#")) {
                    result += "MULTILINESTRING ("
                } else {
                    result += "LINESTRING "
                }
                val data = points.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for (i in data.indices) {
                    if (data[i].isEmpty()) {
                        continue
                    }
                    result += "("
                    val data2 =
                        data[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (j in data2.indices) {
                        if (data2[j].isEmpty()) {
                            continue
                        }
                        val point = data2[j].split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (point.size != 2) {
                            continue
                        }
                        result += point[0] + " " + point[1] + ","
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length - 1)
                    }
                    result += "),"
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length - 1)
                }
                if (points.contains("#")) {
                    result += ")"
                } else {
                    result += ""
                }
//                Log.e("yimi", "pointsToWktString(线): $result")
                return result


            } else {
                //面
                if (points == null || points.isEmpty()) {
                    return ""
                }
                var result = ""
                //            'polygon ((10 10, 10 20, 20 20, 20 15, 10 10))'  ==    10,10;20,20
                //            'multipolygon (((10 10, 10 20, 20 20, 20 15 , 10 10), (50 40, 50 50, 60 50, 60 40, 50 40)))' 10,10;20,20#10,10;20,20
                if (points.contains("#")) {
                    result += "multipolygon (("
                } else {
                    result += "polygon ("
                }
                val data = points.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for (i in data.indices) {
                    if (data[i].isEmpty()) {
                        continue
                    }
                    result += "("
                    val data2 =
                        data[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (j in data2.indices) {
                        if (data2[j].isEmpty()) {
                            continue
                        }
                        val point = data2[j].split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (point.size != 2) {
                            continue
                        }
                        result += point[0] + " " + point[1] + ","
                    }

                    if (result.endsWith(",")) {
                        result = result.substring(0, result.length - 1)
                    }
                    result += "),"
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length - 1)
                }
                if (points.contains("#")) {
                    result += "))"
                } else {
                    result += ")"
                }
//                Log.e("yimi", "pointsToWktString(面): $result")
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }



}