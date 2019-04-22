package com.nisco.family.common.callback;

import java.util.List;

/**
 * @author : cathy
 * @package : com.nisco.family.common.callback
 * @time : 2019/02/20
 * @desc :
 * @version: 1.0
 */

public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
