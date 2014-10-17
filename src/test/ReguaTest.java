package test;

import java.util.Vector;

import junit.framework.TestCase;
import main.Search;
import main.Search.Regua;

/**
 * @author pisaruk
 * 
 */
public class ReguaTest extends TestCase {
	Search.Regua regua;

	/**
	 * @param name
	 */
	public ReguaTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		regua = new Search.Regua("src/puz1");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link Search.Regua#hashCode()}.
	 */
	public void testHashCode() {

	}

	/**
	 * Test method for {@link Search.Regua#deslocar(Search.Regua, int)}.
	 */
	public void testDeslocar() {

	}

	/**
	 * Test method for {@link Search.Regua#clone()}.
	 */
	public void testClone() {

	}

	/**
	 * Test method for {@link Search.Regua#Regua(java.lang.String)}.
	 */
	public void testRegua() {

	}

	public void testGetDeslocamentosPossiveis() {
		Regua regua = new Regua("-AABB", 2);
		assertEquals(regua.getDeslocamentosPossiveis(), new int[] { 1, 2 });
		regua.desloca(1);
		assertEquals(regua.getDeslocamentosPossiveis(), new int[] { -1, 1, 2 });
		regua.desloca(1);
		assertEquals(regua.getDeslocamentosPossiveis(), new int[] { -1, 1, -2,
				2 });
		regua.desloca(1);
		assertEquals(regua.getDeslocamentosPossiveis(), new int[] { -1, 1 - 2 });
		regua.desloca(1);
		assertEquals(regua.getDeslocamentosPossiveis(), new int[] { -1, -2 });
	}

	private boolean assertEquals(int[] a, int[] b) {
		if (a.length != b.length)
			return false;
		int i;
		for (i = 0; i < a.length && a[i] == b[i]; i++)
			;
		return i == a.length;
	}

	/**
	 * Test method for {@link Search.Regua#desloca(int)}.
	 */
	public void testDesloca() {
		int deslocamento[] = new int[100];
		Search.Regua regua = new Search.Regua("AABB-", 2);
		Search.Regua reguaInicial = new Search.Regua("AABB-", 2);
		for (int i = 0; i < 100; i++) {
			deslocamento[i] = (int) (Math.random()
					* regua.getMaxDeslocamentoParaDireita() + regua
					.getMaxDeslocamentoParaEsquerda());
			regua.desloca(deslocamento[i]);
		}
		for (int i = deslocamento.length - 1; i >= 0; i--)
			regua.desloca(-deslocamento[i]);
		assertEquals(regua, reguaInicial);
	}

	/**
	 * Test method for {@link Search.Regua#toString()}.
	 */
	public void testToString() {
	}

	/**
	 * Test method for {@link Search.Regua#isEstadoMeta()}.
	 */
	public void testIsEstadoMeta() {

	}

	/**
	 * System.out.println("Not yet implemented"); Test method for
	 * {@link Search.Regua#getMaxDeslocamentoParaEsquerda()}.
	 */
	public void testGetMinDeslocamento() {

	}

	/**
	 * Test method for {@link Search.Regua#getMaxDeslocamentoParaDireita()}.
	 */
	public void testGetMaxDeslocamento() {

	}

	/**
	 * Test method for {@link Search.Regua#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {

	}

	/**
	 * Test method for {@link Search.Regua#asString()}.
	 */
	public void testAsString() {

	}

	/**
	 * Test method for {@link Search.Regua#getMetasPossiveis(int)}.
	 */
	public void testGetMetasPossiveis() {
	}

	/**
	 * Test method for {@link Search.Regua#getDiff(Search.Regua)}.
	 */
	public void testGetDiff() {

	}

	/**
	 * Test method for {@link Search.Regua#getMinDiff()}.
	 */
	public void testGetMinDiff() {

	}

	/**
	 * Test method for {@link Search.Regua#getTamanho()}.
	 */
	public void testGetTamanho() {
		Search.Regua regua1 = new Search.Regua("-AABB", 2);
		assertEquals(regua1.getTamanho(), 5);
		Search.Regua regua2 = new Search.Regua("-AAABBB", 3);
		assertEquals(regua2.getTamanho(), 7);
	}

	/**
	 * Test method for {@link Search.Regua#getDistanciaTrocas(Search.Regua)}.
	 */
	public void testGetDistanciaTrocas() {
		Search.Regua regua = new Search.Regua("-AABB", 2);
		assertEquals(regua.getDistanciaTrocas(), 8);
		regua.desloca(1);
		assertEquals(regua.getDistanciaTrocas(), 10);
		regua.desloca(1);
		assertEquals(regua.getDistanciaTrocas(), 12);
		regua.desloca(1);
		assertEquals(regua.getDistanciaTrocas(), 10);
		Vector metas = Regua.getMetasPossiveis(2);
		for (int i = 0; i < metas.size(); i++)
			assertEquals(((Regua) metas.elementAt(i)).getDistanciaTrocas(), 0);
	}

	public void testGeraReguasAleatoriamente() {
		Vector reguas = Search.Regua.geraReguasAleatoriamente(2, 120);
		assertEquals(reguas.size(), 120);
	}

	/**
	 * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	public void testEqualsObject1() {

	}

	/**
	 * Test method for {@link java.lang.Object#clone()}.
	 */
	public void testClone1() {

	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	public void testToString1() {

	}
}
