package com.algoboss.erp.util.nfe;

public class ChaveAcessoNFe {
	/**
	 * 
	 * @param cUF -Código da UF do emitente do Documento Fiscal.
	 * @param dataAAMM -Ano e Mês de emissão da NF-e.
	 * @param cnpjCpf -CNPJ do emitente.
	 * @param mod -Modelo do Documento Fiscal.
	 * @param serie -Série do Documento Fiscal.
	 * @param tpEmis -Forma de emissão da NF-e.
	 * @param nNF -Número do Documento Fiscal.
	 * @param cNF -Código Numérico que compõe a Chave de Acesso.
	 */
	public static String generate(String cUF, String dataAAMM, String cnpjCpf, String mod, String serie, String tpEmis, String nNF, String cNF) {
		StringBuilder chave = new StringBuilder();
        try {
        	chave.append(lpadTo(cUF, 2, '0'));
        	chave.append(lpadTo(dataAAMM, 4, '0'));
        	chave.append(lpadTo(cnpjCpf.replaceAll("\\D",""), 14, '0'));
        	chave.append(lpadTo(mod, 2, '0'));
        	chave.append(lpadTo(serie, 3, '0'));
        	chave.append(lpadTo(String.valueOf(nNF), 9, '0'));
        	chave.append(lpadTo(tpEmis, 1, '0'));
        	chave.append(lpadTo(cNF, 8, '0'));
        	chave.append(modulo11(chave.toString()));

        	//chave.insert(0, "NFe");

        	info("Chave NF-e: " + chave.toString());
        } catch (Exception e) {
        	error(e.toString());
		}
        return chave.toString();
    }
	
	public static int modulo11(String chave) {
        int total = 0;
        int peso = 2;
            
        for (int i = 0; i < chave.length(); i++) {
            total += (chave.charAt((chave.length()-1) - i) - '0') * peso;
            peso ++;
            if (peso == 10)
                peso = 2;
        }
        int resto = total % 11;
        return (resto == 0 || resto == 1) ? 0 : (11 - resto);
    }

	public static String lpadTo(String input, int width, char ch) {
        String strPad = "";

        StringBuffer sb = new StringBuffer(input.trim());
        while (sb.length() < width)
            sb.insert(0,ch);
        strPad = sb.toString();
        
        if (strPad.length() > width) {
        	strPad = strPad.substring(0,width);
        }
        return strPad;
    }

	/**
	 * Log ERROR.
	 * @param error
	 */
	private static void error(String error) {
		System.out.println("| ERROR: " + error);
	}

	/**
	 * Log INFO.
	 * @param info
	 */
	private static void info(String info) {
		System.out.println("| INFO: " + info);
	}

}

