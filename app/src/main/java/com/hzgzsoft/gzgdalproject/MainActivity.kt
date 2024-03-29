package com.hzgzsoft.gzgdalproject

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.hzgzsoft.gzgdallib.model.Map_Feature
import com.hzgzsoft.gzgdallib.model.ZZYShp
import com.hzgzsoft.gzgdallib.utils.ShpFileUtils_Java
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener {

            Log.e("yimi","test:chick")

            testCreateShp_Polyon()


        }

    }


    fun testCreateShp_Polyon(){

        val dataList: ArrayList<Map_Feature> = ArrayList()

        val  mapFeature01 = Map_Feature()
        mapFeature01.points = "119.38632499,28.15385465;119.38493879,28.15417115;119.38596560,28.15578788;119.38766841,28.15509499;119.38766841,28.15387175;119.38632499,28.15385465;#119.38649613,28.15417115;119.38691541,28.15434223;119.38714644,28.15482126;119.38653035,28.15509499;119.38579447,28.15454753;119.38649613,28.15417115;#119.38882601,28.15428938;119.38838533,28.15456311;119.38886879,28.15535009;119.38964746,28.15509774;119.38935225,28.15429793;119.38882601,28.15428938;#119.38848986,28.15534306;119.38739459,28.15601884;119.38831872,28.15707100;119.38899471,28.15658341;119.38890914,28.15564246;119.38848986,28.15534306;#119.38844708,28.15576222;119.38869522,28.15612149;119.38867811,28.15652353;119.38819037,28.15642088;119.38802779,28.15593330;119.38844708,28.15576222;"
        mapFeature01.fType = "2"
        mapFeature01.remark = "测试数据1"
        mapFeature01.number = "331181000000001"
        mapFeature01.iYear = 11111
        mapFeature01.area = 11.11
        dataList.add(mapFeature01)

        val  mapFeature02 = Map_Feature()
        mapFeature02.points = "119.38338145,28.15429091;119.38245732,28.15465018;119.38313331,28.15607872;119.38428847,28.15536017;119.38338145,28.15429091;#119.38514415,28.15565957;119.38410022,28.15578788;119.38340712,28.15637811;119.38401465,28.15684003;119.38512704,28.15645510;119.38514415,28.15565957;"
        mapFeature02.fType = "2"
        mapFeature02.remark = "test2"
        mapFeature02.number = "331181000000002"
        mapFeature02.iYear = 22222
        mapFeature02.area = 22.22
        dataList.add(mapFeature02)

        val  mapFeature03 = Map_Feature()
        mapFeature03.points = "119.38172143,28.15684003;119.38089998,28.15626691;119.38214072,28.15559969;119.38258567,28.15574511;119.38290227,28.15672883;119.38172143,28.15684003;"
        mapFeature03.fType = "2"
        mapFeature03.remark = "test3"
        mapFeature03.number = "331181000000003"
        mapFeature03.iYear = 33333
        mapFeature03.area = 33.33
        dataList.add(mapFeature03)

        val  mapFeature04 = Map_Feature()
        mapFeature04.points = "119.38326773,28.15695438;119.38227816,28.15726980;119.38305261,28.15816587;119.38437204,28.15804401;119.38381272,28.15726980;119.38326773,28.15695438;#119.38284465,28.15739166;119.38349720,28.15741317;119.38308846,28.15782895;119.38284465,28.15739166;"
        mapFeature04.fType = "2"
        mapFeature04.remark = "test4"
        mapFeature04.number = "331181000000004"
        mapFeature04.iYear = 44444
        mapFeature04.area = 44.44
        dataList.add(mapFeature04)




        val path:String = getExternalFilesDir("")!!.absolutePath + "/" //android -> data -> 包名


        //根据类型自动生成属性字段(Map_Feature)
//        ShpFileUtils_Java.createShpByList(this,path,"自动获取字段测试_Map.shp",dataList,
//            true,false,null,null,null, null);



//        ShpFileUtils_Java.createShp_BySecond_Polygon(this,path,"test2.shp",dataList)


        //导出征占格式测试(按照征占系统导入模板字段)
        val zzyShp = ZZYShp()
        zzyShp.points = "119.38326773,28.15695438;119.38227816,28.15726980;119.38305261,28.15816587;119.38437204,28.15804401;119.38381272,28.15726980;119.38326773,28.15695438;#119.38284465,28.15739166;119.38349720,28.15741317;119.38308846,28.15782895;119.38284465,28.15739166;"
        val zzyShpList: ArrayList<Any> = ArrayList()
        zzyShpList.add(zzyShp)

        ShpFileUtils_Java.createShpByList(this,path,"导出征占shp.shp",zzyShpList,
            true,false,null,null,null, null,"1");


    }



}
