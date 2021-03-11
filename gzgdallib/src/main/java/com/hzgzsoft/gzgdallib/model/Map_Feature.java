package com.hzgzsoft.gzgdallib.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Map_Feature implements Parcelable {
    private int Id;
    private String CodeId;
    private String TownId;
    private String TownName;
    private String VillageId;
    private String VillageName;
    private String GroupId;
    private String GroupName;
    private String UserName;
    private String LType;
    private String Second;
    private String Third;
    private String Number;
    private double Area;
    private double Len;
    private String FType;
    private String Points;
    private int NumOfPts;
    private double EMinX;
    private double EMinY;
    private double EMaxX;
    private double EMaxY;
    private double CPtX;
    private double CPtY;
    private String Remark;
    private String PYM;
    private String Label;
    private String Label1;
    private int IYear;
    private String InsertTime;
    private String STypeAtt;
    private String IsMark;
    private String Edit;
    private String State;
    private String PointsJWD;  //用于区分是否是下载的数据  1 为下载过来的数据
    private String EMinXJWD;   //新增百万亩省/市抽查 增加了MobileNo字段,用于记录抽查前图斑编号,(即抽查的那个图斑的number),平板端保存到这个字段,上传时给到MobileNo字段
    private String EMinYJWD;
    private String EMaxXJWD;
    private String EMaxYJWD;
    private String CPtXJWD;
    private String CPtYJWD;
    private String HasPic;
    private String SHI_QUAN_DNo;
    private String DesignUnitNo;
    private String DesignUnitName;
    private boolean isCheck = false;

    //yimi:20200611 营造林等部分增加了CodeName值,平板就不区分了,直接都加上  暂时默认设置为空字符串,避免出错
    private String codename = "";//县市名称

    //20200914 yimi: 新增百万亩用于记录抽查前数的图斑号,平板用 EMinXJWD 暂存,上传时读取这个值
    private String mobileno = "";


    public Map_Feature(Map_Feature obj) {
        Id = obj.getId();
        CodeId = obj.getCodeId();
        TownId = obj.getTownId();
        TownName = obj.getTownName();
        VillageId = obj.getVillageId();
        VillageName = obj.getVillageName();
        GroupId = obj.getGroupId();
        GroupName = obj.getGroupName();
        UserName = obj.getUserName();
        this.LType = obj.getLType();
        Second = obj.getSecond();
        Third = obj.getThird();
        Number = obj.getNumber();
        Area = obj.getArea();
        Len = obj.getLen();
        this.FType = obj.getFType();
        Points = obj.getPoints();
        NumOfPts = obj.getNumOfPts();
        this.EMinX = obj.getEMinX();
        this.EMinY = obj.getEMinY();
        this.EMaxX = obj.getEMaxX();
        this.EMaxY = obj.getEMaxY();
        this.CPtX = obj.getCPtX();
        this.CPtY = obj.getCPtY();
        Remark = obj.getRemark();
        this.PYM = obj.getPYM();
        Label = obj.getLabel();
        Label1 = obj.getLabel1();
        this.IYear = obj.getIYear();
        InsertTime = obj.getInsertTime();
        this.STypeAtt = obj.getSTypeAtt();
        IsMark = obj.getIsMark();
        this.codename = obj.getCodename();
        this.mobileno = obj.getMobileno();
        this.EMinXJWD = obj.getEMinXJWD();
    }


    public Map_Feature(int id, String codeId, String townId, String townName, String villageId,
                       String villageName, String groupId, String groupName, String userName,
                       String LType, String second, String third, String number, double area,
                       double len, String FType, String points, int numOfPts, double EMinX,
                       double EMinY, double EMaxX, double EMaxY, double CPtX, double CPtY,
                       String remark, String PYM, String label, String label1, int IYear,
                       String insertTime, String STypeAtt, String isMark, String codename,
                       String mobileno, String EMinXJWD) {
        Id = id;
        CodeId = codeId;
        TownId = townId;
        TownName = townName;
        VillageId = villageId;
        VillageName = villageName;
        GroupId = groupId;
        GroupName = groupName;
        UserName = userName;
        this.LType = LType;
        Second = second;
        Third = third;
        Number = number;
        Area = area;
        Len = len;
        this.FType = FType;
        Points = points;
        NumOfPts = numOfPts;
        this.EMinX = EMinX;
        this.EMinY = EMinY;
        this.EMaxX = EMaxX;
        this.EMaxY = EMaxY;
        this.CPtX = CPtX;
        this.CPtY = CPtY;
        Remark = remark;
        this.PYM = PYM;
        Label = label;
        Label1 = label1;
        this.IYear = IYear;
        InsertTime = insertTime;
        this.STypeAtt = STypeAtt;
        IsMark = isMark;
        this.codename = codename;
        this.mobileno = mobileno;
        this.EMinXJWD = EMinXJWD;
    }

    protected Map_Feature(Parcel in) {
        Id = in.readInt();
        CodeId = in.readString();
        TownId = in.readString();
        TownName = in.readString();
        VillageId = in.readString();
        VillageName = in.readString();
        GroupId = in.readString();
        GroupName = in.readString();
        UserName = in.readString();
        LType = in.readString();
        Second = in.readString();
        Third = in.readString();
        Number = in.readString();
        Area = in.readDouble();
        Len = in.readDouble();
        FType = in.readString();
        Points = in.readString();
        NumOfPts = in.readInt();
        EMinX = in.readDouble();
        EMinY = in.readDouble();
        EMaxX = in.readDouble();
        EMaxY = in.readDouble();
        CPtX = in.readDouble();
        CPtY = in.readDouble();
        Remark = in.readString();
        PYM = in.readString();
        Label = in.readString();
        Label1 = in.readString();
        IYear = in.readInt();
        InsertTime = in.readString();
        STypeAtt = in.readString();
        IsMark = in.readString();
        Edit = in.readString();
        codename = in.readString();
        mobileno = in.readString();
        EMinXJWD = in.readString();
    }

    public static final Creator<Map_Feature> CREATOR = new Creator<Map_Feature>() {
        @Override
        public Map_Feature createFromParcel(Parcel in) {
            return new Map_Feature(in);
        }

        @Override
        public Map_Feature[] newArray(int size) {
            return new Map_Feature[size];
        }
    };


    public double getArea() {
        return Area;
    }

    public void setArea(double area) {
        Area = area;
    }

    public double getLen() {
        return Len;
    }

    public void setLen(double len) {
        Len = len;
    }

    public double getEMinX() {
        return EMinX;
    }

    public void setEMinX(double EMinX) {
        this.EMinX = EMinX;
    }

    public double getEMinY() {
        return EMinY;
    }

    public void setEMinY(double EMinY) {
        this.EMinY = EMinY;
    }

    public double getEMaxX() {
        return EMaxX;
    }

    public void setEMaxX(double EMaxX) {
        this.EMaxX = EMaxX;
    }

    public double getEMaxY() {
        return EMaxY;
    }

    public void setEMaxY(double EMaxY) {
        this.EMaxY = EMaxY;
    }

    public double getCPtX() {
        return CPtX;
    }

    public void setCPtX(double CPtX) {
        this.CPtX = CPtX;
    }

    public double getCPtY() {
        return CPtY;
    }

    public void setCPtY(double CPtY) {
        this.CPtY = CPtY;
    }

    public Map_Feature() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCodeId() {
        return CodeId;
    }

    public void setCodeId(String codeId) {
        CodeId = codeId;
    }

    public String getTownId() {
        return TownId;
    }

    public void setTownId(String townId) {
        TownId = townId;
    }

    public String getTownName() {
        return TownName;
    }

    public void setTownName(String townName) {
        TownName = townName;
    }

    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(String villageId) {
        VillageId = villageId;
    }

    public String getVillageName() {
        return VillageName;
    }

    public void setVillageName(String villageName) {
        VillageName = villageName;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLType() {
        return LType;
    }

    public void setLType(String LType) {
        this.LType = LType;
    }

    public String getSecond() {
        return Second;
    }

    public void setSecond(String second) {
        Second = second;
    }

    public String getThird() {
        return Third;
    }

    public void setThird(String third) {
        Third = third;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getFType() {
        return FType;
    }

    public void setFType(String FType) {
        this.FType = FType;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }

    public int getNumOfPts() {
        return NumOfPts;
    }

    public void setNumOfPts(int numOfPts) {
        NumOfPts = numOfPts;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getPYM() {
        return PYM;
    }

    public void setPYM(String PYM) {
        this.PYM = PYM;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getLabel1() {
        return Label1;
    }

    public void setLabel1(String label1) {
        Label1 = label1;
    }

    public int getIYear() {
        return IYear;
    }

    public void setIYear(int IYear) {
        this.IYear = IYear;
    }

    public String getInsertTime() {
        return InsertTime;
    }

    public void setInsertTime(String insertTime) {
        InsertTime = insertTime;
    }

    public String getSTypeAtt() {
        return STypeAtt;
    }

    public void setSTypeAtt(String STypeAtt) {
        this.STypeAtt = STypeAtt;
    }

    public String getIsMark() {
        return IsMark;
    }

    public void setIsMark(String isMark) {
        IsMark = isMark;
    }

    public String getEdit() {
        return Edit;
    }

    public void setEdit(String edit) {
        Edit = edit;
    }

    public String getPointsJWD() {
        return PointsJWD;
    }

    public void setPointsJWD(String pointsJWD) {
        PointsJWD = pointsJWD;
    }

    public String getEMinXJWD() {
        return EMinXJWD;
    }

    public void setEMinXJWD(String EMinXJWD) {
        this.EMinXJWD = EMinXJWD;
    }

    public String getEMinYJWD() {
        return EMinYJWD;
    }

    public void setEMinYJWD(String EMinYJWD) {
        this.EMinYJWD = EMinYJWD;
    }

    public String getEMaxXJWD() {
        return EMaxXJWD;
    }

    public void setEMaxXJWD(String EMaxXJWD) {
        this.EMaxXJWD = EMaxXJWD;
    }

    public String getEMaxYJWD() {
        return EMaxYJWD;
    }

    public void setEMaxYJWD(String EMaxYJWD) {
        this.EMaxYJWD = EMaxYJWD;
    }

    public String getCPtXJWD() {
        return CPtXJWD;
    }

    public void setCPtXJWD(String CPtXJWD) {
        this.CPtXJWD = CPtXJWD;
    }

    public String getCPtYJWD() {
        return CPtYJWD;
    }

    public void setCPtYJWD(String CPtYJWD) {
        this.CPtYJWD = CPtYJWD;
    }

    public String getDesignUnitNo() {
        return DesignUnitNo;
    }

    public void setDesignUnitNo(String designUnitNo) {
        DesignUnitNo = designUnitNo;
    }

    public String getDesignUnitName() {
        return DesignUnitName;
    }

    public void setDesignUnitName(String designUnitName) {
        DesignUnitName = designUnitName;
    }

    public String getSHI_QUAN_DNo() {
        return SHI_QUAN_DNo;
    }

    public void setSHI_QUAN_DNo(String SHI_QUAN_DNo) {
        this.SHI_QUAN_DNo = SHI_QUAN_DNo;
    }

    public String getHasPic() {
        return HasPic;
    }

    public void setHasPic(String hasPic) {
        HasPic = hasPic;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    @Override
    public String toString() {
        return "Map_Feature{" +
                "Id=" + Id +
                ", CodeId='" + CodeId + '\'' +
                ", TownId='" + TownId + '\'' +
                ", TownName='" + TownName + '\'' +
                ", VillageId='" + VillageId + '\'' +
                ", VillageName='" + VillageName + '\'' +
                ", GroupId='" + GroupId + '\'' +
                ", GroupName='" + GroupName + '\'' +
                ", UserName='" + UserName + '\'' +
                ", LType='" + LType + '\'' +
                ", Second='" + Second + '\'' +
                ", Third='" + Third + '\'' +
                ", Number='" + Number + '\'' +
                ", Area=" + Area +
                ", Len=" + Len +
                ", FType='" + FType + '\'' +
                ", Points='" + Points + '\'' +
                ", NumOfPts=" + NumOfPts +
                ", EMinX=" + EMinX +
                ", EMinY=" + EMinY +
                ", EMaxX=" + EMaxX +
                ", EMaxY=" + EMaxY +
                ", CPtX=" + CPtX +
                ", CPtY=" + CPtY +
                ", Remark='" + Remark + '\'' +
                ", PYM='" + PYM + '\'' +
                ", Label='" + Label + '\'' +
                ", Label1='" + Label1 + '\'' +
                ", IYear=" + IYear +
                ", InsertTime='" + InsertTime + '\'' +
                ", STypeAtt='" + STypeAtt + '\'' +
                ", IsMark='" + IsMark + '\'' +
                ", Edit='" + Edit + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeString(CodeId);
        parcel.writeString(TownId);
        parcel.writeString(TownName);
        parcel.writeString(VillageId);
        parcel.writeString(VillageName);
        parcel.writeString(GroupId);
        parcel.writeString(GroupName);
        parcel.writeString(UserName);
        parcel.writeString(LType);
        parcel.writeString(Second);
        parcel.writeString(Third);
        parcel.writeString(Number);
        parcel.writeDouble(Area);
        parcel.writeDouble(Len);
        parcel.writeString(FType);
        parcel.writeString(Points);
        parcel.writeInt(NumOfPts);
        parcel.writeDouble(EMinX);
        parcel.writeDouble(EMinY);
        parcel.writeDouble(EMaxX);
        parcel.writeDouble(EMaxY);
        parcel.writeDouble(CPtX);
        parcel.writeDouble(CPtY);
        parcel.writeString(Remark);
        parcel.writeString(PYM);
        parcel.writeString(Label);
        parcel.writeString(Label1);
        parcel.writeInt(IYear);
        parcel.writeString(InsertTime);
        parcel.writeString(STypeAtt);
        parcel.writeString(IsMark);
        parcel.writeString(Edit);
        parcel.writeString(codename);
        parcel.writeString(mobileno);
        parcel.writeString(EMinXJWD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Map_Feature feature = (Map_Feature) o;

        if (feature.getNumber() == null || feature.getPoints() == null) {
            return false;
        }

        if (this.getNumber().equals(feature.getNumber()) && this.getPoints().equals(feature.getPoints())) {
            return true;
        }
        return super.equals(o);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Id;
        result = 31 * result + (CodeId != null ? CodeId.hashCode() : 0);
        result = 31 * result + (TownId != null ? TownId.hashCode() : 0);
        result = 31 * result + (TownName != null ? TownName.hashCode() : 0);
        result = 31 * result + (VillageId != null ? VillageId.hashCode() : 0);
        result = 31 * result + (VillageName != null ? VillageName.hashCode() : 0);
        result = 31 * result + (GroupId != null ? GroupId.hashCode() : 0);
        result = 31 * result + (GroupName != null ? GroupName.hashCode() : 0);
        result = 31 * result + (UserName != null ? UserName.hashCode() : 0);
        result = 31 * result + (LType != null ? LType.hashCode() : 0);
        result = 31 * result + (Second != null ? Second.hashCode() : 0);
        result = 31 * result + (Third != null ? Third.hashCode() : 0);
        result = 31 * result + (Number != null ? Number.hashCode() : 0);
        temp = Double.doubleToLongBits(Area);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Len);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (FType != null ? FType.hashCode() : 0);
        result = 31 * result + (Points != null ? Points.hashCode() : 0);
        result = 31 * result + NumOfPts;
        temp = Double.doubleToLongBits(EMinX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(EMinY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(EMaxX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(EMaxY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(CPtX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(CPtY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (Remark != null ? Remark.hashCode() : 0);
        result = 31 * result + (PYM != null ? PYM.hashCode() : 0);
        result = 31 * result + (Label != null ? Label.hashCode() : 0);
        result = 31 * result + (Label1 != null ? Label1.hashCode() : 0);
        result = 31 * result + IYear;
        result = 31 * result + (InsertTime != null ? InsertTime.hashCode() : 0);
        result = 31 * result + (STypeAtt != null ? STypeAtt.hashCode() : 0);
        result = 31 * result + (IsMark != null ? IsMark.hashCode() : 0);
        result = 31 * result + (Edit != null ? Edit.hashCode() : 0);
        result = 31 * result + (State != null ? State.hashCode() : 0);
        result = 31 * result + (PointsJWD != null ? PointsJWD.hashCode() : 0);
        result = 31 * result + (EMinXJWD != null ? EMinXJWD.hashCode() : 0);
        result = 31 * result + (EMinYJWD != null ? EMinYJWD.hashCode() : 0);
        result = 31 * result + (EMaxXJWD != null ? EMaxXJWD.hashCode() : 0);
        result = 31 * result + (EMaxYJWD != null ? EMaxYJWD.hashCode() : 0);
        result = 31 * result + (CPtXJWD != null ? CPtXJWD.hashCode() : 0);
        result = 31 * result + (CPtYJWD != null ? CPtYJWD.hashCode() : 0);
        result = 31 * result + (HasPic != null ? HasPic.hashCode() : 0);
        result = 31 * result + (SHI_QUAN_DNo != null ? SHI_QUAN_DNo.hashCode() : 0);
        result = 31 * result + (DesignUnitNo != null ? DesignUnitNo.hashCode() : 0);
        result = 31 * result + (DesignUnitName != null ? DesignUnitName.hashCode() : 0);
        result = 31 * result + (codename != null ? codename.hashCode() : 0);
        result = 31 * result + (mobileno != null ? mobileno.hashCode() : 0);
        return result;
    }

}
