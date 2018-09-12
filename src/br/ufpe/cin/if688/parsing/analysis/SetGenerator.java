package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator {

    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

        if (g == null) throw new NullPointerException("g nao pode ser nula.");

        Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);

        Set<GeneralSymbol> firsts;
        Collection<Production> rules = g.getProductions();
        List<GeneralSymbol> stack;
        List<Integer> indexes;
        int index;

        for (Production e: rules) {
            firsts = new HashSet<>();
            stack = new ArrayList<>();
            indexes = new ArrayList<>();
            stack.add(e.getNonterminal());
            indexes.add(0);
            stack.add(e.getProduction().get(0));
            indexes.add(0);
            index = 1;
            while (!(stack.isEmpty() && index >= stack.size())) {
                if (stack.get(index) instanceof Nonterminal) {
                    if (!first.get(stack.get(index)).isEmpty()){
                        firsts.addAll(first.get(e.getNonterminal()));
                    } else {
                        for (Production p : rules) {
                            if (p.getNonterminal() == stack.get(index)) {
                                stack.add(p.getProduction().get(0));
                                indexes.add(0);
                            }
                        }
                    }
                    index++;
                } else {
                    if (!first.get(e.getNonterminal()).isEmpty()){
                        firsts.addAll(first.get(e.getNonterminal()));
                    }
                    firsts.add(stack.get(index));
                    index++;
                    if (index >= stack.size()) {
                        if (firsts.contains(SpecialSymbol.EPSILON)) {
                            GeneralSymbol aux;
                            int auxiliary;
                            do {
                                aux = stack.remove(stack.size()-1);
                                auxiliary = indexes.remove(indexes.size()-1);
                                index--;
                            } while (!(aux instanceof Nonterminal));
                            if (aux != e.getNonterminal()) {
                                for (Production p : rules) {
                                    if (auxiliary < p.getProduction().size() && p.getProduction().get(auxiliary) == aux) {
                                        auxiliary++;
                                        aux = p.getNonterminal();
                                        for (Production h : rules) {
                                            if (h.getNonterminal() == aux) {
                                                if (auxiliary >= h.getProduction().size()) {
                                                    stack.clear();
                                                    indexes.clear();
                                                    break;
                                                } else {
                                                    firsts.remove(SpecialSymbol.EPSILON);
                                                    stack.add(h.getProduction().get(auxiliary));
                                                    indexes.add(auxiliary);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            stack.clear();
                            indexes.clear();
                        }
                    }
                }
            }
            first.put(e.getNonterminal(),firsts);
        }
        System.out.println(first);

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
