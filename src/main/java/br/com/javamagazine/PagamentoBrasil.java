package br.com.javamagazine;

import java.math.BigDecimal;

public class PagamentoBrasil implements Pagamento {

	@Override
	public BigDecimal calcularTaxaImposto(BigDecimal valorPagamento) {
		return new BigDecimal("0.75");
	}

	@Override
	public void efetuarPagamento(BigDecimal valorPagamento) {
		BigDecimal taxaImposto = calcularTaxaImposto(valorPagamento);
		BigDecimal pagamento = valorPagamento.multiply(taxaImposto).add(valorPagamento);
		System.out.println(pagamento.toPlainString());
	}
	
}
