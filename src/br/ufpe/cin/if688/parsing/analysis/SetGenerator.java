package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator {

    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

        if (g == null) throw new NullPointerException("g nao pode ser nula.");

        Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);

        Set<GeneralSymbol> firstTemp;
        Collection<Production> rules = g.getProductions();
        List<GeneralSymbol> stack;
        List<Integer> indexes;
        int index;

        for (Production e: rules) {
            firstTemp = new HashSet<>();
            stack = new ArrayList<>();
            indexes = new ArrayList<>();
            stack.add(e.getNonterminal());
            indexes.add(0);
            stack.add(e.getProduction().get(0));
            indexes.add(0);
            index = 1;
            while (!(index >= stack.size())) {
                if (stack.get(index) instanceof Nonterminal) {
                    if (!first.get(stack.get(index)).isEmpty()){
                        firstTemp.addAll(first.get(e.getNonterminal()));
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
                        firstTemp.addAll(first.get(e.getNonterminal()));
                    }
                    firstTemp.add(stack.get(index));
                    index++;
                    if (index >= stack.size()) {
                        if (firstTemp.contains(SpecialSymbol.EPSILON)) {
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
                                                    firstTemp.remove(SpecialSymbol.EPSILON);
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
            first.put(e.getNonterminal(),firstTemp);
        }
        //System.out.println(first);

        return first;

    }


    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {

        if (g == null || first == null)
            throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);

        Set<GeneralSymbol> followTemp;
        Collection<Production> rules = g.getProductions();
        List<Production> stack;
        List<Integer> indexes;
        int index;

        //System.out.println(rules);

        for (Production e : rules) {
            followTemp = new HashSet<>();
            stack = new ArrayList<>();
            indexes = new ArrayList<>();
            index =0;

            if (g.getStartSymbol()==e.getNonterminal()){
                followTemp.add(SpecialSymbol.EOF);
            }

            for (Production p : rules) {
                if (p.getProduction().contains(e.getNonterminal())){
                    indexes.add(p.getProduction().indexOf(e.getNonterminal()));
                    stack.add(p);
                }
            }

            while (!(index >= stack.size())) {
                if (indexes.get(index) >= (stack.get(index).getProduction().size() -1)) {
                    if ((stack.get(index).getProduction().get(indexes.get(index)) != stack.get(index).getNonterminal())){
                        if (follow.get(stack.get(index).getNonterminal()).isEmpty()) {
                            for (Production p : rules) {
                                if (p.getProduction().contains(stack.get(index).getNonterminal())){
                                    indexes.add(p.getProduction().indexOf(stack.get(index).getNonterminal()));
                                    stack.add(p);
                                }
                            }
                        } else {
                            followTemp.addAll(follow.get(stack.get(index).getNonterminal()));
                        }
                    }
                } else {
                    GeneralSymbol auxiliary = stack.get(index).getProduction().get(indexes.get(index) + 1);
                    if (auxiliary instanceof Nonterminal) {
                        if (first.get(auxiliary).isEmpty()) {
                            for (Production p : rules) {
                                if (p.getProduction().contains(auxiliary)){
                                    indexes.add(p.getProduction().indexOf(auxiliary));
                                    stack.add(p);
                                }
                            }
                        } else {
                            if (first.get(auxiliary).contains(SpecialSymbol.EPSILON)) {
                                if ((indexes.get(index)+2) > (stack.get(index).getProduction().size() -1)) {
                                    for (Production p : rules) {
                                        if (p.getProduction().contains(stack.get(index).getNonterminal())){
                                            indexes.add(p.getProduction().indexOf(stack.get(index).getNonterminal()));
                                            stack.add(p);
                                        }
                                    }
                                } else {
                                    indexes.add(indexes.get(index) + 1);
                                    stack.add(stack.get(index));
                                }
                                followTemp.addAll(first.get(auxiliary));
                                followTemp.remove(SpecialSymbol.EPSILON);
                            } else {
                                followTemp.addAll(first.get(auxiliary));
                            }
                        }
                    } else {
                        followTemp.add(auxiliary);
                    }
                }
                index++;
            }

            follow.put(e.getNonterminal(),followTemp);
        }

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
