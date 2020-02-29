package com.kincony.kbox.net.data;

import com.kincony.kbox.R;
import com.kincony.kbox.utils.IPUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * 设备实体类
 */
@Entity(
        tableName = "device",
        foreignKeys = @ForeignKey(entity = NetAddress.class,
                parentColumns = "id",
                childColumns = "address_id",
                onDelete = CASCADE))
public class Device {
    /**
     * ip地址id
     */
    @ColumnInfo(name = "address_id")
    public int addressId;
    /**
     * 设备id
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "device_id")
    public int deviceId;
    /**
     * 排序字段
     */
    @ColumnInfo(name = "index_n")
    public int index;
    /**
     * 编号（几路）
     */
    public int number;
    /**
     * 是否是打开状态
     */
    public boolean open = false;
    /**
     * 名称（可以编辑修改）
     */
    public String name;
    /**
     * icon
     */
    public int icon = R.drawable.icon16;
    /**
     * 关联的地址
     */
    @Ignore
    public NetAddress address;


    public Device() {
    }

    public Device(NetAddress address, int number, int index) {
        this.address = address;
        this.addressId = address.getId();
        this.number = number;
        this.name = IPUtils.INSTANCE.getDefaultName(address, number);
        this.index = index;
    }

}