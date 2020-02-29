package com.kincony.kbox.net.data.database;

import android.content.Context;

import com.kincony.kbox.net.data.Device;
import com.kincony.kbox.net.data.NetAddress;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Device.class, NetAddress.class}, version = 2, exportSchema = false)
public abstract class KBoxDatabase extends RoomDatabase {
    public static final String DB_NAME = "CompanyDatabase.db";
    private static volatile KBoxDatabase instance;

    public static synchronized KBoxDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static KBoxDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                KBoxDatabase.class,
                DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(SupportSQLiteDatabase database) {

                    }
                })
                .build();
    }

    public abstract DeviceDao getDeviceDao();

    public abstract AddressDao getAddressDao();
}
