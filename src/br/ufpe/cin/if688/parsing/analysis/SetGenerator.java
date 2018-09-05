package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator {

    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

        if (g == null) throw new NullPointerException("g nao pode ser nula.");

        Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);

        Set<GeneralSymbol> firsts;
        List<GeneralSymbol> productions;
        Nonterminal variable;
        Collection<Production> rules = g.getProductions();

        for (Production e: rules) {
            firsts = new HashSet<>();
            variable = e.getNonterminal();
            productions = e.getProduction();
            GeneralSymbol s = productions.get(0);
            while (!(s instanceof Nonterminal)) {
                for (Production p : rules) {
                    if (p.getNonterminal() == s && variable != p.getNonterminal()) {
                        s = p.getProduction().get(0);
                    }
                }
            }
            firsts.add(s);
            first.put(variable,firsts);
            System.out.println(first);
        }

        return first;

    }


    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {

        if (g == null || first == null)
            throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);

        /*
         * implemente aqui o método para retornar o conjunto follow
         */

        return follow;
    }

    //método para inicializar mapeamento nãoterminais -> conjunto de símbolos
    private static Map<Nonterminal, Set<GeneralSymbol>>
    initializeNonterminalMapping(Grammar g) {
        Map<Nonterminal, Set<GeneralSymbol>> result =
                new HashMap<Nonterminal, Set<GeneralSymbol>>();

        for (Nonterminal nt: g.getNonterminals())
            result.put(nt, new HashSet<GeneralSymbol>());

        return result;
    }

}
