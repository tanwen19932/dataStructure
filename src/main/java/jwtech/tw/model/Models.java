package jwtech.tw.model;


/**
 * @author TW
 * @date TW on 2016/11/25.
 */
public class Models {
    public static TF_IDFModel getTF_IDFModel() {
        return TF_IDFModel.getInstance();
    }

    public static MIModel getMIModel() {
        return MIModel.getInstance();
    }
}
