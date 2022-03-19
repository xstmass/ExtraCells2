package mekanism.api.reactor;


public interface IReactorBlock {
    boolean isFrame();

    IFusionReactor getReactor();

    void setReactor(IFusionReactor reactor);

}
