package com.xjtu.meshine.mcloudsdk.component;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xjtu.meshine.mcloudsdk.db.DBInfo;
import com.xjtu.meshine.mcloudsdk.db.DaoFactory;
import com.xjtu.meshine.mcloudsdk.db.DbSqlite;
import com.xjtu.meshine.mcloudsdk.db.IBaseDao;

/**
 * Created by Meshine on 17/1/5.
 */
public class DBManager {

    private Context context;

    IBaseDao<MethodModel> methodDAO;

    private static DBManager ourInstance = null;

    public static DBManager getInstance() {
        if (ourInstance == null){
            synchronized (DBManager.class){
                if (ourInstance == null){
                    ourInstance = new DBManager();
                }
            }
        }
        return ourInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 初始化数据库管理器
     * @param context
     */
    public void initialize(Context context){
        this.context = context;

        //创建数据库,初始化对象
        SQLiteDatabase db = context.openOrCreateDatabase(DBInfo.BD_NAME,DBInfo.DB_TYPE,null);
        DbSqlite dbSqlite = new DbSqlite(context,db);
        methodDAO = DaoFactory.createGenericDao(dbSqlite,MethodModel.class);
    }

    /**
     * 加入一条记录
     * @param method
     */
    public void insert(MethodModel method){
        methodDAO.insert(method);
    }



    /**
     * 删除一条记录
     * @param method
     */
    public void delete(MethodModel method){
        methodDAO.delete("method_id=?",method.getId());
    }


    /**
     * 更新一条记录
     * @param method
     */
    public void update(MethodModel method){
        methodDAO.update(method,"method_id=?",method.getId());
    }

    /**
     * 根据条件查询
     * @param selection
     * @param selectionArgs
     * @return
     */
    public MethodModel getMethod(String selection, String... selectionArgs){
        return methodDAO.queryFirstRecord(selection,selectionArgs);
    }

}
