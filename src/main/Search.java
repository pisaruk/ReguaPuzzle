package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * EP1 de Introducao a Inteligencia Artifical - 2006 </br>
 * 
 * nome: Fabio Pisaruk. NUSP: 3467959
 */
public class Search {
	static int repeticoes = 0;

	public static boolean debugEnabled = false;

	public interface Buscador {
		public Vector getSucessores(No obj);

		public boolean isEstadoMeta(No obj);

		public Resposta buscar(No noOrigem, EstrategiaDeBusca estrategia);
	}

	public static class BuscadorDaRegua implements Buscador {
		private static final Buscador instance = new BuscadorDaRegua();

		public static Buscador getInstance() {
			return instance;
		}

		private BuscadorDaRegua() {
		}

		public Vector getSucessores(No no) {
			Regua regua = (Regua) no.getObj();
			int[] deslocamentos = regua.getDeslocamentosPossiveis();
			Vector nosGerados = new Vector(deslocamentos.length);
			for (int i = 0; i < deslocamentos.length; i++)
				nosGerados.add(new No(no.getCusto()
						+ Math.abs(deslocamentos[i]), Regua.deslocar(regua,
						deslocamentos[i]), no));

			return nosGerados;
		}

		public boolean isEstadoMeta(No obj) {
			return ((Regua) obj.getObj()).isEstadoMeta();
		}

		public Resposta buscar(No noOrigem, EstrategiaDeBusca estrategia) {
			return estrategia.alcancarMeta(noOrigem, this);
		}
	}

	public static interface EstrategiaDeBusca {
		public Resposta alcancarMeta(No noOrigem, Buscador buscador);

		public String getNome();
	}

	public static class Resposta {
		private int numNosExpandidos;

		private int numNosGerados;

		private Vector nosDaSolucao;

		private int profundidadedaMeta;

		private int custoSolucao;

		private EstrategiaDeBusca estrategiaDeBusca;

		public Resposta(int nosExpandidos, int nosGerados, No noMeta,
				EstrategiaDeBusca estrategiaDeBusca) {
			numNosExpandidos = nosExpandidos;
			numNosGerados = nosGerados;
			nosDaSolucao = noMeta.getCaminho();
			custoSolucao = noMeta.getCusto();
			profundidadedaMeta = noMeta.getProfundidade();
			this.estrategiaDeBusca = estrategiaDeBusca;
		}

		public int getNumNosExpandidos() {
			return numNosExpandidos;
		}

		public int getNumNosGerados() {
			return numNosGerados;
		}

		public int getTamanho() {
			return nosDaSolucao.size() - 1;
		}

		public int getCustoSolucao() {
			return custoSolucao;
		}

		public double getFatorMedioRamificacao() {
			return (double) numNosGerados / numNosExpandidos;
		}

		public Vector getNosDaSolucao() {
			return nosDaSolucao;
		}

		public int getProfundidadeDaMeta() {
			return profundidadedaMeta;
		}

		public EstrategiaDeBusca getEstrategiaDeBusca() {
			return estrategiaDeBusca;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Resposta>");
			sb.append(getNosDaSolucao());
			sb.append("</Resposta>");
			return sb.toString();
		}
	}

	public static class EstrategiaDeBuscaEmLargura implements EstrategiaDeBusca {
		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			Set nosAnalisados = new HashSet();
			LinkedList nosNaoAnalisados = new LinkedList();
			nosNaoAnalisados.addLast(noOrigem);
			do {
				No noEmAnalise = (No) nosNaoAnalisados.removeFirst();
				if (debugEnabled)
					System.out.println(noEmAnalise);
				if (buscador.isEstadoMeta(noEmAnalise))
					return new Resposta(nosAnalisados.size(), nosAnalisados
							.size()
							+ nosNaoAnalisados.size(), noEmAnalise, this);
				nosAnalisados.add(noEmAnalise);
				Vector sucessores = buscador.getSucessores(noEmAnalise);
				if (debugEnabled)
					System.out.println(sucessores);
				sucessores.removeAll(nosAnalisados);
				Iterator i = sucessores.iterator();
				while (i.hasNext()) {
					No noAtual = (No) i.next();
					nosNaoAnalisados.addLast(noAtual);
				}
			} while (!nosNaoAnalisados.isEmpty());
			return null;
		}

		public String getNome() {
			return "BrFS";
		}
	}

	public static class EstrategiaDeBuscaEmProfundidade implements
			EstrategiaDeBusca {

		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			HashMap nosAnalisados = new HashMap();
			LinkedList nosNaoAnalisados = new LinkedList();
			nosNaoAnalisados.addLast(noOrigem);
			do {
				No noEmAnalise = (No) nosNaoAnalisados.removeLast();
				if (debugEnabled)
					System.out.println("(" + noEmAnalise + ")");
				if (buscador.isEstadoMeta(noEmAnalise))
					return new Resposta(nosAnalisados.size(), nosAnalisados
							.size()
							+ nosNaoAnalisados.size(), noEmAnalise, this);
				nosAnalisados.put(noEmAnalise, noEmAnalise);
				Vector sucessores = buscador.getSucessores(noEmAnalise);
				sucessores.removeAll(nosNaoAnalisados);
				for (Iterator iter = sucessores.iterator(); iter.hasNext();) {
					No no = (No) iter.next();
					if (nosAnalisados.containsKey(no)) {
						No noJaAnalisado = (No) nosAnalisados.get(no);
						if (noJaAnalisado.getProfundidade() > no
								.getProfundidade()) {
							nosAnalisados.remove(noJaAnalisado);
							nosNaoAnalisados.addLast(no);
						}
					} else
						nosNaoAnalisados.addLast(no);
				}
				if (debugEnabled)
					System.out.println(sucessores);
			} while (!nosNaoAnalisados.isEmpty());
			return null;
		}

		public String getNome() {
			return "DFS";
		}

	}

	public static class EstrategiaDeBuscaEmProfundidadeIterativa implements
			EstrategiaDeBusca {

		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			int profundidadeMaxima = 0;
			int nosGerados = 0;
			int nosExpandidos = 0;
			while (true) {
				HashMap nosAnalisados = new HashMap();
				LinkedList nosNaoAnalisados = new LinkedList();
				nosNaoAnalisados.addLast(noOrigem);
				if (debugEnabled)
					System.out.println("Profundidade maxima="
							+ profundidadeMaxima);
				while (!nosNaoAnalisados.isEmpty()) {
					No noEmAnalise = (No) nosNaoAnalisados.removeLast();
					if (debugEnabled)
						System.out.println("(" + noEmAnalise + ")");
					if (buscador.isEstadoMeta(noEmAnalise)) {
						nosExpandidos += nosAnalisados.size();
						nosGerados += nosNaoAnalisados.size();
						return new Resposta(nosExpandidos, nosGerados,
								noEmAnalise, this);
					}

					if (noEmAnalise.getProfundidade() < profundidadeMaxima) {
						nosAnalisados.put(noEmAnalise, noEmAnalise);
						Vector sucessores = buscador.getSucessores(noEmAnalise);
						sucessores.removeAll(nosNaoAnalisados);
						for (Iterator iter = sucessores.iterator(); iter
								.hasNext();) {
							No no = (No) iter.next();
							if (nosAnalisados.containsKey(no)) {
								No noJaAnalisado = (No) nosAnalisados.get(no);
								if (noJaAnalisado.getProfundidade() > no
										.getProfundidade()) {
									nosAnalisados.remove(noJaAnalisado);
									nosNaoAnalisados.addLast(no);
								}
							} else
								nosNaoAnalisados.addLast(no);
						}
						if (debugEnabled)
							System.out.println(sucessores);
					}
				}
				nosGerados += nosAnalisados.size() + nosNaoAnalisados.size();
				nosExpandidos += nosAnalisados.size();
				profundidadeMaxima++;
			}
		}

		public String getNome() {
			return "IDS";
		}

	}

	public static class EstrategiaDeBuscaCustoUniforme implements
			EstrategiaDeBusca {

		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			Set nosAnalisados = new HashSet();
			TreeSet nosNaoAnalisados = new TreeSet(new ComparadorDeNoPorCusto());
			nosNaoAnalisados.add(noOrigem);
			do {
				No noEmAnalise = (No) nosNaoAnalisados.first();
				nosNaoAnalisados.remove(noEmAnalise);
				if (buscador.isEstadoMeta(noEmAnalise))
					return new Resposta(nosAnalisados.size(), nosAnalisados
							.size()
							+ nosNaoAnalisados.size(), noEmAnalise, this);
				if (debugEnabled)
					System.out.println("(" + noEmAnalise + ")");
				nosAnalisados.add(noEmAnalise);
				Vector sucessores = buscador.getSucessores(noEmAnalise);
				if (debugEnabled)
					System.out.println(sucessores);
				sucessores.removeAll(nosAnalisados);
				nosNaoAnalisados.addAll(sucessores);
			} while (!nosNaoAnalisados.isEmpty());
			return null;
		}

		public String getNome() {
			return "UCS";
		}
	}

	public static class EstrategiaDeBuscaAEstrela implements EstrategiaDeBusca {

		private Heuristica heuristica;

		public EstrategiaDeBuscaAEstrela(Heuristica heuristica) {
			this.heuristica = heuristica;
		}

		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			Set nosAnalisados = new HashSet();
			TreeSet nosNaoAnalisados = new TreeSet(
					new ComparadorDeNoPorCustoEHeuristica(heuristica));
			nosNaoAnalisados.add(noOrigem);
			do {
				No noEmAnalise = (No) nosNaoAnalisados.first();
				nosNaoAnalisados.remove(noEmAnalise);
				if (debugEnabled)
					System.out.println("(" + noEmAnalise + ")");
				if (buscador.isEstadoMeta(noEmAnalise))
					return new Resposta(nosAnalisados.size(), nosAnalisados
							.size()
							+ nosNaoAnalisados.size(), noEmAnalise, this);
				nosAnalisados.add(noEmAnalise);
				Vector sucessores = buscador.getSucessores(noEmAnalise);
				if (debugEnabled)
					System.out.println(sucessores);
				sucessores.removeAll(nosAnalisados);
				nosNaoAnalisados.addAll(sucessores);
			} while (!nosNaoAnalisados.isEmpty());
			return null;
		}

		public String getNome() {
			return "A*";
		}
	}

	public static class EstrategiaDeBuscaIDAEstrela implements
			EstrategiaDeBusca {
		private Heuristica heuristica;

		public EstrategiaDeBuscaIDAEstrela(Heuristica heuristica) {
			this.heuristica = heuristica;
		}

		public Resposta alcancarMeta(No noOrigem, Buscador buscador) {
			int novoCustoMaximo = heuristica.valor(noOrigem);
			int nosGerados = 0;
			int nosExpandidos = 0;
			while (true) {
				int custoMaximo = novoCustoMaximo;
				novoCustoMaximo = Integer.MAX_VALUE;
				HashMap nosAnalisados = new HashMap();
				LinkedList nosNaoAnalisados = new LinkedList();
				nosNaoAnalisados.addLast(noOrigem);
				if (debugEnabled)
					System.out.println("Custo Maximo = " + custoMaximo);
				do {
					No noEmAnalise = (No) nosNaoAnalisados.removeLast();
					if (debugEnabled)
						System.out.println("\t\t(" + noEmAnalise + ")");
					int custo = noEmAnalise.getCustoTotal(heuristica);
					if (custo <= custoMaximo) {
						nosAnalisados.put(noEmAnalise, noEmAnalise);
						if (buscador.isEstadoMeta(noEmAnalise))
							return new Resposta(nosExpandidos, nosGerados,
									noEmAnalise, this);
						nosExpandidos++;
						Vector sucessores = buscador.getSucessores(noEmAnalise);
						sucessores.removeAll(nosNaoAnalisados);
						for (Iterator iter = sucessores.iterator(); iter
								.hasNext();) {
							No no = (No) iter.next();
							if (nosAnalisados.containsKey(no)) {
								No noJaAnalisado = (No) nosAnalisados.get(no);
								if (noJaAnalisado.getCusto() > no.getCusto()) {
									nosAnalisados.remove(noJaAnalisado);
									nosNaoAnalisados.addLast(no);
								}
							} else
								nosNaoAnalisados.addLast(no);
						}
						if (debugEnabled)
							System.out.println(sucessores);
						nosGerados += sucessores.size();
					} else if (custo < novoCustoMaximo)
						novoCustoMaximo = custo;
				} while (!nosNaoAnalisados.isEmpty());
			}
		}

		public String getNome() {
			return "IDA*";
		}
	}

	public static class ComparadorDeNoPorCusto implements Comparator {

		public int compare(Object arg0, Object arg1) {
			No noA = (No) arg0;
			No noB = (No) arg1;

			if (noB.getObj().equals(noA.getObj()))
				return 0;
			if (noA.getCusto() == noB.getCusto()) {
				if (noA.getId() > noB.getId())
					return -1;
				return 1;
			} else
				return noA.getCusto() - noB.getCusto();
		}
	}

	public static class ComparadorDeNoPorCustoEHeuristica implements Comparator {
		private Heuristica heuristica;

		public ComparadorDeNoPorCustoEHeuristica(Heuristica heuristica) {
			this.heuristica = heuristica;
		}

		public int compare(Object arg0, Object arg1) {
			if (arg0.equals(arg1))
				return 0;
			No noA = (No) arg0;
			No noB = (No) arg1;
			int fnA = noA.getCustoTotal(heuristica);
			int fnB = noB.getCustoTotal(heuristica);
			if (fnA == fnB) {
				int h2A = noA.getCusto(); // HeuristicaDiffParaRegua.getInstance().valor(noA);
				int h2B = noB.getCusto();// HeuristicaDiffParaRegua.getInstance().valor(noB);
				if (h2A == h2B) {
					if (noA.getProfundidade() == noB.getProfundidade()) {
						repeticoes++;
						return (int) (noB.getId() - noA.getId());
					} else
						return noA.getProfundidade() - noB.getProfundidade();
				} else
					return h2A - h2B;
			} else
				return fnA - fnB;
		}
	}

	public static interface Heuristica {
		public int valor(No no);
	}

	public static class HeuristicaDiffParaRegua implements Heuristica {
		private static HeuristicaDiffParaRegua instance = new HeuristicaDiffParaRegua();

		private HeuristicaDiffParaRegua() {
		}

		public static HeuristicaDiffParaRegua getInstance() {
			return instance;
		}

		public int valor(No no) {
			return ((Regua) no.getObj()).getMinDiff();
		}
	}

	public static class HeuristicaDistanciaParaRegua implements Heuristica {
		private static HeuristicaDistanciaParaRegua instance = new HeuristicaDistanciaParaRegua();

		private HeuristicaDistanciaParaRegua() {
		}

		public static HeuristicaDistanciaParaRegua getInstance() {
			return instance;
		}

		public int valor(No no) {
			int valor = ((Regua) no.getObj()).getDistanciaTrocas();
			/*
			 * if (no.getAntecessor()!=null){ int valorPai =
			 * valor(no.getAntecessor()); return Math.min(valor, valorPai); }
			 */
			return valor;
		}
	}

	public static class No {
		private int custo;

		private Object obj;

		private long id;

		private static long next_id = 0;

		private No antecessor;

		private int profundidade;

		public static void reiniciarContadorDeID() {
			// TODO É necessario?
			synchronized (No.class) {
				next_id = 0;
			}
		}

		public No getAntecessor() {
			return antecessor;
		}

		public long getId() {
			return id;
		}

		public int getProfundidade() {
			return profundidade;
		}

		public No(int cost, Object obj, No antecessor) {
			this.custo = cost;
			this.obj = obj;
			this.antecessor = antecessor;
			if (antecessor == null)
				profundidade = 0;
			else
				profundidade = antecessor.profundidade + 1;
			// synchronized (No.class) {
			this.id = next_id++;
			// }
		}

		public int getCusto() {
			return custo;
		}

		/**
		 * Retorna o caminho reverso deste no ao n� origem caminhando pelos
		 * antecessores.
		 * 
		 * @return Vector contendo os n�s que formam o caminho reverso do n�
		 *         atual � raiz.
		 */
		public Vector getCaminho() {
			Vector nos = new Vector();
			No noAtual = this;
			while (noAtual.getAntecessor() != null) {
				nos.add(noAtual);
				noAtual = noAtual.getAntecessor();
			}
			nos.add(noAtual);
			for (int i = 0; i < nos.size() / 2; i++) {
				No noSwap = (No) nos.elementAt(i);
				nos.setElementAt(nos.elementAt(nos.size() - i - 1), i);
				nos.setElementAt(noSwap, nos.size() - i - 1);
			}
			return nos;

		}

		public int getCustoTotal(Heuristica heuristica) {
			return heuristica.valor(this) + custo;
		}

		public Object getObj() {
			return obj;
		}

		public boolean equals(Object outro) {
			No outroNo = (No) outro;
			return outroNo.getObj().equals(obj);
		}

		public int hashCode() {
			return obj.hashCode();
		}

		public String toString() {
			return "<" + obj.toString() + " g=" + custo + " id=" + id + " h1="
					+ HeuristicaDistanciaParaRegua.getInstance().valor(this)
					+ ">";
		}
	}

	public static class Regua {
		private char regua[];

		private String strRegua;

		private int posicaoDoVazio;

		private int hashCode;

		private int distTrocas = -1;

		public static Regua deslocar(Regua regua, int deslocamento) {
			try {
				Regua nova = (Regua) regua.clone();
				nova.desloca(deslocamento);
				return nova;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		protected Object clone() throws CloneNotSupportedException {
			return new Regua(regua);
		}

		private Regua(char regua[]) {
			this.regua = new char[regua.length];
			for (int i = 0; i < regua.length; i++) {
				this.regua[i] = regua[i];
				if (regua[i] == '-')
					posicaoDoVazio = i;
			}
			strRegua = String.valueOf(regua);
			hashCode = strRegua.hashCode();
		}

		/**
		 * Controi uma Regua a partir dos dados do aruivo passado
		 * 
		 * @param fileName
		 *            Nome do arquivo
		 */
		public Regua(String fileName) {
			BufferedReader bf;
			try {
				bf = new BufferedReader(new FileReader(fileName));
				bf.readLine();
				String r = bf.readLine();
				strRegua = r;
				hashCode = strRegua.hashCode();
				posicaoDoVazio = r.indexOf('-');
				regua = r.toCharArray();
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Arquivo nao existente ou com formato invalido.", e);
			}
		}

		/**
		 * Constroi uma regua a partir de uma sequencia de caracteres e um
		 * tamanho
		 * 
		 * TODO validar regua
		 * 
		 * @param strRegua
		 * @param numeroDeBlocos
		 *            Por exemplo: a regua AA-BB possui 4 blocos
		 */
		public Regua(String strRegua, int numeroDeBlocos) {
			posicaoDoVazio = strRegua.indexOf('-');
			regua = strRegua.toCharArray();
			this.strRegua = strRegua;
			hashCode = strRegua.hashCode();
		}

		/**
		 * TODO implementar
		 * 
		 * @param regua
		 * @return
		 */
		private boolean reguaValida(char[] regua) {
			return true;
		}

		/**
		 * 
		 * @param n
		 */
		public void desloca(int n) {
			if (deslocamentoPossivel(n)) {
				regua[posicaoDoVazio] = regua[posicaoDoVazio + n];
				posicaoDoVazio += n;
				regua[posicaoDoVazio] = '-';
				strRegua = String.valueOf(regua);
				hashCode = strRegua.hashCode();
				distTrocas = -1;
			}
		}

		/**
		 * Verifica se um dado deslocamento é permitido
		 * 
		 * @param i
		 * @return
		 */
		private boolean deslocamentoPossivel(int i) {
			return (Math.abs(i) <= getNumeroDeBlocos()) && (i != 0)
					&& (posicaoDoVazio + i >= 0)
					&& (posicaoDoVazio + i < regua.length);
		}

		public String toString() {
			return strRegua;
		}

		/**
		 * Retorna verdadeiro se esta regua representa um estado meta
		 * 
		 * TODO melhorar isso considerando os dois casos da posicao do espaco
		 * 
		 * @return
		 */
		public boolean isEstadoMeta() {
			int contB = 0;
			for (int i = 0; i < regua.length && regua[i] != 'A'; i++)
				if (regua[i] == 'B')
					contB++;
			return contB == getNumeroDeBlocos();
		}

		private int getNumeroDeBlocos() {
			return (regua.length - 1) / 2;
		}

		/**
		 * Retorna o maior deslocamento permnitido para a esquerda
		 * 
		 * @return
		 */
		public int getMaxDeslocamentoParaEsquerda() {
			return Math.max(-getNumeroDeBlocos(), -posicaoDoVazio);
		}

		/**
		 * Retorna o maximo deslocamento permitido para a direita
		 * 
		 * @return
		 */
		public int getMaxDeslocamentoParaDireita() {
			return Math.min(getNumeroDeBlocos(), regua.length - posicaoDoVazio
					- 1);
		}

		public boolean equals(Object obj) {
			if (obj.hashCode() == hashCode())
				return ((Regua) obj).asString().equals(asString());
			return false;
		}

		public int hashCode() {
			return hashCode;
		}

		public String asString() {
			return strRegua;
		}

		/**
		 * Controi todas as metas possiveis para um dado tamanho de regua
		 * 
		 * @param numeroDeBlocos
		 */
		public static Vector getMetasPossiveis(int numBlocos) {
			int size = numBlocos * 2 + 1;
			Vector reguas = new Vector(size);
			char regChar[] = new char[size];
			int i;
			for (i = 0; i < numBlocos; i++)
				regChar[i] = 'B';
			regChar[i++] = '-';
			for (; i < regChar.length; i++)
				regChar[i] = 'A';
			Regua reguaMetaBase = new Regua(regChar);
			for (i = reguaMetaBase.getMaxDeslocamentoParaEsquerda(); i <= reguaMetaBase
					.getMaxDeslocamentoParaDireita(); i++)
				reguas.add(Regua.deslocar(reguaMetaBase, i));
			return reguas;
		}

		/**
		 * Retorna o numero de blocos que diferem entre esta regua e a passada
		 * como parametro
		 * 
		 * @return
		 */
		private int getDiff(Regua outra) {
			char reguaChar[] = outra.asString().toCharArray();
			int numeroDePosicoesDiferentes = 0;
			for (int i = 0; i < outra.getTamanho(); i++) {
				if (reguaChar[i] != regua[i])
					numeroDePosicoesDiferentes++;
			}
			return numeroDePosicoesDiferentes;
		}

		public int getMinDiff() {
			Vector respostas = getMetasPossiveis(getNumeroDeBlocos());
			int min = Integer.MAX_VALUE;
			for (Iterator iter = respostas.iterator(); iter.hasNext();) {
				int diff = ((Regua) iter.next()).getDiff(this);
				if (diff < min)
					min = diff;
			}
			return min;
		}

		public int getTamanho() {
			return this.regua.length;
		}

		/**
		 * 
		 * @param regua
		 * @return
		 */
		public int getDistanciaTrocas() {
			if (distTrocas == -1)
				distTrocas = calculaDistanciaTrocas();
			return distTrocas;
		}

		private int calculaDistanciaTrocas() {
			// System.out.println("<calculaDistanciaTrocas()>");
			int custo = 0;
			int ub = strRegua.lastIndexOf('B');
			int pa = strRegua.indexOf('A');
			int ta = getNumeroDeBlocos();
			int tb = getNumeroDeBlocos();
			while (pa <= getNumeroDeBlocos() && pa >= 0) {
				// System.out.println("custo="+custo);
				custo += ta - pa;
				ta++;
				pa = strRegua.indexOf('A', pa + 1);
			}
			while (ub >= getNumeroDeBlocos()) {
				// System.out.println("custo="+custo);
				custo += ub - tb;
				tb--;
				ub = strRegua.lastIndexOf('B', ub - 1);
			}

			// System.out.println("<calculaDistanciaTrocas() custo=" + custo);
			return custo;
		}

		/**
		 * 
		 * @param numeroDeBlocos
		 * @param numDeReguas
		 * @return
		 */
		public static Vector geraReguasAleatoriamente(int numeroDeBlocos,
				int numDeReguas) {
			Random random = new Random(System.currentTimeMillis());
			Vector reguas = new Vector(numDeReguas);
			char regua[] = new char[numeroDeBlocos * 2 + 1];
			while (reguas.size() < numDeReguas) {
				regua[numeroDeBlocos * 2] = '-';
				int i = 0;
				int numBlocosBrancos = 0;
				for (; i < numeroDeBlocos * 2
						&& numBlocosBrancos < numeroDeBlocos
						&& i - numBlocosBrancos < numeroDeBlocos; i++) {

					if (random.nextBoolean()) {
						regua[i] = 'B';
						numBlocosBrancos++;
					} else
						regua[i] = 'A';
				}
				char corBlocoFaltante;
				if (numBlocosBrancos < numeroDeBlocos)
					corBlocoFaltante = 'B';
				else
					corBlocoFaltante = 'A';
				while (i < numeroDeBlocos * 2)
					regua[i++] = corBlocoFaltante;
				int posicaoEspacoVazio = (int) ((numeroDeBlocos * 2 + 1) * Math
						.random());
				char swap = regua[posicaoEspacoVazio];
				regua[posicaoEspacoVazio] = '-';
				regua[numeroDeBlocos * 2] = swap;
				reguas.add(new Regua(String.valueOf(regua), numeroDeBlocos));
			}
			return reguas;
		}

		public int[] getDeslocamentosPossiveis() {
			int min = getMaxDeslocamentoParaEsquerda();
			int max = getMaxDeslocamentoParaDireita();
			int deslocamentos[] = new int[2 * Math.min(-min, max)
					+ Math.abs(max + min)];
			if (min == 0) {
				for (int i = 1; i <= max; i++)
					deslocamentos[i - 1] = i;
			} else if (max == 0) {
				for (int i = -1; i >= min; i--)
					deslocamentos[-i - 1] = i;
			} else if (-min > max) {
				for (int i = 1; i <= max; i++) {
					deslocamentos[2 * i - 2] = -i;
					deslocamentos[2 * i - 1] = i;
				}
				for (int i = -(max + 1); i >= min; i--)
					deslocamentos[max - i - 1] = i;
			} else {
				for (int i = 1; i <= -min; i++) {
					deslocamentos[2 * i - 2] = -i;
					deslocamentos[2 * i - 1] = i;
				}
				for (int i = -min + 1; i <= max; i++)
					deslocamentos[i - min - 1] = i;
			}
			return deslocamentos;
		}
	}

	public static void main(String[] args) {
		if (args.length == 0)
			imprimeInfoUso();

		for (int i = 0; i < args.length; i++)
			if (args[i].equalsIgnoreCase("-debug")) {
				debugEnabled = true;
				break;
			}

		if (args[0].equalsIgnoreCase("benchmark"))
			benchmark();
		else {
			if (!((args.length == 2 && !debugEnabled) || (args.length == 3 && debugEnabled)))
				imprimeInfoUso();
			String estrategiaDeBusca = args[1];
			String nomeDoArquivo = args[0];
			Resposta resposta = null;
			No noRaiz = new No(0, new Regua(nomeDoArquivo), null);

			long iniTime = System.currentTimeMillis();

			if (estrategiaDeBusca.equalsIgnoreCase("brfs"))
				resposta = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmLargura());
			else if (estrategiaDeBusca.equalsIgnoreCase("dfs"))
				resposta = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmProfundidade());
			else if (estrategiaDeBusca.equalsIgnoreCase("ucs"))
				resposta = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaCustoUniforme());
			else if (estrategiaDeBusca.equalsIgnoreCase("ids"))
				resposta = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmProfundidadeIterativa());
			else if (estrategiaDeBusca.equalsIgnoreCase("a*"))
				resposta = BuscadorDaRegua.getInstance().buscar(
						noRaiz,
						new EstrategiaDeBuscaAEstrela(
								new HeuristicaDistanciaParaRegua()));
			else if (estrategiaDeBusca.equalsIgnoreCase("ida*"))
				resposta = BuscadorDaRegua.getInstance().buscar(
						noRaiz,
						new EstrategiaDeBuscaIDAEstrela(
								new HeuristicaDistanciaParaRegua()));
			else {
				imprimeInfoUso();
			}
			System.out.println("Solu��o: ");
			Vector caminho = resposta.getNosDaSolucao();
			for (Iterator iter = caminho.iterator(); iter.hasNext();) {
				System.out.println((No) iter.next());
			}
			System.out.println("numero de n�s visitados............"
					+ resposta.getNumNosExpandidos());
			System.out.println("numero de n�s gerados.............."
					+ resposta.getNumNosGerados());
			System.out.println("profundidade da meta..............."
					+ resposta.getProfundidadeDaMeta());
			System.out.println("custo da solucao..................."
					+ resposta.getCustoSolucao());
			System.out.println("fator de ramifica��o m�dio........."
					+ resposta.getFatorMedioRamificacao());
			System.out.println("tamanho da solu��o................."
					+ resposta.getTamanho());
			System.out.println("tempo de execucao.................."
					+ (System.currentTimeMillis() - iniTime) + "ms");
			System.out.println("repeticoes: " + repeticoes);
		}
	}

	private static void imprimeInfoUso() {
		System.out
				.println("Uso: java main.Main (<arquivo> <estrategia de busca>) | benchmark [-debug]");
		System.out.println("Estrat�gias de busca:");
		System.out.println("BrFS - Busca em largura");
		System.out.println("DFS - Busca em profundidade");
		System.out.println("UCS - Busca com custo uniforme");
		System.out.println("IDS - Busca em profundidade iterativa");
		System.out.println("A* - Busca A*");
		System.out.println("IDA* - Busca IDA*");
		System.out.println("Exemplos: ");
		System.out.println("java main.Main puz1 BrFS");
		System.out.println("java main.Main puz2 A*");
		System.out.println("java main.Main puz2 A* -debug");
		System.out.println("java main.Main benchmark");
		System.exit(-1);
	}

	private static void benchmark() {
		int numReguasInicial = 64;
		int numReguas = numReguasInicial;
		Vector reguasGlobal[] = new Vector[5];

		for (int i = 2; i <= 6; i++) {
			reguasGlobal[i - 2] = Regua.geraReguasAleatoriamente(i, numReguas);
			numReguas /= 2;
		}
		System.out
				.println("estrategia;numBlocos;custo;profundidade;tamanho;visitados;gerados;fatorRamificacao;tempo");

		for (int i = 2; i <= 6; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();
			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmLargura());
			}
			imprimeResultado(i, respostas, iniTime);
		}
		for (int i = 2; i <= 6; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();
			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmProfundidade());
			}
			imprimeResultado(i, respostas, iniTime);
		}

		for (int i = 2; i <= 5; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();

			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaCustoUniforme());
			}
			imprimeResultado(i, respostas, iniTime);
		}

		for (int i = 2; i <= 6; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();
			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(noRaiz,
						new EstrategiaDeBuscaEmProfundidadeIterativa());
			}
			imprimeResultado(i, respostas, iniTime);
		}
		for (int i = 2; i <= 6; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();
			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(
						noRaiz,
						new EstrategiaDeBuscaAEstrela(
								HeuristicaDistanciaParaRegua.getInstance()));
			}
			imprimeResultado(i, respostas, iniTime);
		}

		for (int i = 2; i <= 6; i++) {
			Vector reguas = reguasGlobal[i - 2];
			Resposta respostas[] = new Resposta[reguas.size()];
			int h = 0;
			long iniTime = System.currentTimeMillis();
			for (Iterator iter = reguas.iterator(); iter.hasNext(); h++) {
				No noRaiz = new No(0, iter.next(), null);
				respostas[h] = BuscadorDaRegua.getInstance().buscar(
						noRaiz,
						new EstrategiaDeBuscaIDAEstrela(
								HeuristicaDistanciaParaRegua.getInstance()));
			}
			imprimeResultado(i, respostas, iniTime);
		}
	}

	private static void imprimeResultado(int numDeBlocos, Resposta[] respostas,
			long iniTime) {
		double medias[] = new double[7];
		medias[6] = (System.currentTimeMillis() - iniTime);
		for (int h = 0; h < respostas.length; h++) {
			medias[0] += respostas[h].getCustoSolucao();
			medias[1] += respostas[h].getProfundidadeDaMeta();
			medias[2] += respostas[h].getTamanho();
			medias[3] += respostas[h].getNumNosExpandidos();
			medias[4] += respostas[h].getNumNosGerados();
		}
		medias[5] = medias[4] / medias[3];
		for (int j = 0; j < 5; j++)
			medias[j] /= respostas.length;

		System.out.println(respostas[0].getEstrategiaDeBusca().getNome() + ";"
				+ numDeBlocos + ";" + medias[0] + ";" + medias[1] + ";"
				+ medias[2] + ";" + medias[3] + ";" + medias[4] + ";"
				+ medias[5] + ";" + medias[6]);
	}

}
