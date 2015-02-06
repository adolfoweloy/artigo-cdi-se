package br.com.javamagazine;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

@Singleton
public class ExtensionUsageTest {

	@Inject
	private Pagamento pagamento;
	
	public void teste(@Observes ContainerInitialized event, 
		@Parameters List<String> parameters) {
		System.out.println("starting a managed cdi application");
		System.out.println(pagamento);
	}
	
}
