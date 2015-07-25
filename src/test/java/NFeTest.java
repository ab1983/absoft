import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.httpclient.protocol.Protocol;
import org.junit.Test;

import com.algoboss.integration.ssl.DynamicSocketFactory;
import com.fincatto.nfe310.FabricaDeObjetosFake;
import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.classes.NFAmbiente;
import com.fincatto.nfe310.classes.NFTipoEmissao;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.webservices.WSFacade;


public class NFeTest {

	@Test
	public void test() {
		
		NFeConfig config = new NFeConfig() {
			
			@Override
			public NFTipoEmissao getTipoEmissao() {
				// TODO Auto-generated method stub
				return NFTipoEmissao.EMISSAO_NORMAL;
			}
			
			@Override
			public String getCertificadoSenha() {
				// TODO Auto-generated method stub PKCS12
				return "digo2000";
			}
			
			@Override
			public File getCertificado() throws IOException {
				// TODO Auto-generated method stub
				return new File("D:\\Documents\\@fiscal\\Backup_Cert_e_Banco\\CertMais.pfx");
			}
			
			@Override
			public File getCadeiaCertificados() throws IOException {
				// TODO Auto-generated method stub
				return new File("D:\\Documents\\@fiscal\\Backup_Cert_e_Banco\\NFeCacerts");
				//return new File("D:\\Documents\\@fiscal\\Backup_Cert_e_Banco\\ac-validrfbv2.p7b");
			}
			
			@Override
			public NFUnidadeFederativa getCUF() {
				// TODO Auto-generated method stub
				return NFUnidadeFederativa.ES;
			}
			
			@Override
			public NFAmbiente getAmbiente() {
				// TODO Auto-generated method stub
				return NFAmbiente.HOMOLOGACAO;
			}
		};
		
		try {
			
 					
			
			NFLoteEnvio lote = FabricaDeObjetosFake.getNFLoteEnvio();
			DynamicSocketFactory.generateSslByNFeConfig(config);
			final NFLoteEnvioRetorno retorno = new WSFacade(config).enviaLote(lote);
			System.out.println(retorno.getInfoRecebimento());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
