package com.leodd.oneweek.DAO;

import android.content.Context;

/**
 * Created by Leodd
 * On 2016/4/17.
 */
public class DAOFactory {
    public static IServerDAO getInstance (Context context) {
        return new ServerDAO(context);
    }
}
