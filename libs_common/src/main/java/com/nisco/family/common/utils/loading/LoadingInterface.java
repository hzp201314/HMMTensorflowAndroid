package com.nisco.family.common.utils.loading;

/**
 * @author wpq
 * @version 1.0
 */
public interface LoadingInterface {

    void showLoading();

    void showNetworkError();

    void showError();

    void showEmpty();

    void dismissLoading();

    interface OnClickListener {
        void onClick();
    }
}
