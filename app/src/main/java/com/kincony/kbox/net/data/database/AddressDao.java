package com.kincony.kbox.net.data.database;


import com.kincony.kbox.net.data.NetAddress;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AddressDao {
    @Query("SELECT * FROM address")
    List<NetAddress> getAllAddress();

    @Query("SELECT * FROM address WHERE ip == :ip AND port == :port")
    NetAddress getAddress(String ip, int port);

    /*当数据库中已经有此用户的时候，直接替换*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAddress(NetAddress... address);

    //删除某一项
    @Delete
    public void delete(NetAddress... address);

}
