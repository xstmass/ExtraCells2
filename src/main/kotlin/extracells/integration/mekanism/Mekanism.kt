//package extracells.integration.mekanism
//
//import extracells.api.ECApi
//import extracells.api.ExtraCellsApi
//import extracells.api.IExternalGasStorageHandler
//import extracells.integration.Integration.Mods
//
//object Mekanism {
//    fun init(): Unit {
//        if (Mods.MEKANISMGAS.isEnabled) {
//            ECApi.instance().addExternalStorageInterface(HandlerMekanismGasTank)
//        }
//    }
//}