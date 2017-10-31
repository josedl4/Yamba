// Autores:
// Martin Martin, Jose Luis
// Martinez Arias, Miguel
package com.example.joselm.yambaandroidtestjl;

/**
 * Enum con los tipos de resultados de la operacion de actualizacion del estado.
 */
public enum OperationStatus {

    SUCCESS(R.string.operation_success),
    NETWORK_FAIL(R.string.operation_network_fail),
    TOKEN_FAIL(R.string.operation_token_fail),
    UNKNOWN(R.string.operation_unknown);

    private int texto;


    OperationStatus(int texto) {
        this.texto = texto;
    }

    /**
     * Devuelve el texto asociado a este estado.
     *
     * @return int del recurso del texto
     */
    public int getTexto() {
        return texto;
    }
}
