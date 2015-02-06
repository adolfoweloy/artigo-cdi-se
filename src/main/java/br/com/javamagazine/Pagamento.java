package br.com.javamagazine;

import java.math.BigDecimal;

public interface Pagamento {

	/**
	 * Consulta valor de imposto para um pagamento.
	 * @param valorPagamento
	 * @return
	 */
	BigDecimal calcularTaxaImposto(BigDecimal valorPagamento);
	
	/**
	 * Efetua um pagamento
	 */
	void efetuarPagamento(BigDecimal valorPagamento);
	
}
