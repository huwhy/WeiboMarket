package cn.huwhy.weibo.robot.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class MyCheckBox {

    CheckBox checkBox = new CheckBox();

    public ObservableValue<CheckBox> getCheckBox() {
        return new ObservableValue<CheckBox>() {

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

            @Override
            public void addListener(ChangeListener<? super CheckBox> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super CheckBox> listener) {

            }

            @Override
            public CheckBox getValue() {
                return checkBox;
            }
        };
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }
}
