package net.igap.network_module;


public interface OnResponse {
    void onReceived(AbstractObject response, AbstractObject error);
}
