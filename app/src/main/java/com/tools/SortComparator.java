package com.tools;

import android.hardware.Camera;

import java.util.Comparator;

/**
 * Created by Fang Ruijiao on 2016/10/13.
 */

public class SortComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        Camera.Size size1 = (Camera.Size)lhs;
        Camera.Size size2 = (Camera.Size)rhs;
        if(size1.width > size2.width)
            return -1;
        else
            return 1;
    }
}
