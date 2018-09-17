package br.ufpe.cin.if688.table;


import br.ufpe.cin.if688.parsing.analysis.*;
import br.ufpe.cin.if688.parsing.grammar.*;
import java.util.*;


public final class Table {
    private Table() {    }

    public static Map<LL1Key, List<GeneralSymbol>> createTable(Grammar g) throws NotLL1Exception {
        if (g == null) throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> first =
                SetGenerator.getFirst(g);
        Map<Nonterminal, Set<GeneralSymbol>> follow =
                SetGenerator.getFollow(g, first);

        Map<LL1Key, List<GeneralSymbol>> parsingTable =
                new HashMap<LL1Key, List<GeneralSymbol>>();

        Collection<Production> rules = g.getProductions();
        Set<GeneralSymbol> fstflwTemp;
        List<GeneralSymbol> stack;
        List<Integer> indexes;
        int index;

        for (Production p : rules) {
            if (p.getProduction().contains(SpecialSymbol.EPSILON)){
                fstflwTemp = follow.get(p.getNonterminal());
                for (GeneralSymbol s : fstflwTemp) {
                    parsingTable.put(new LL1Key(p.getNonterminal(),s),p.getProduction());
                }
            } else {
                stack = new ArrayList<>();
                indexes = new ArrayList<>();
                stack.add(p.getNonterminal());
                indexes.add(0);
                stack.add(p.getProduction().get(0));
                indexes.add(0);
                index = 1;
                while (!(index >= stack.size())) {
                    if (stack.get(index) instanceof Nonterminal) {
                        for (Production e : rules) {
                            if (e.getNonterminal() == stack.get(index)) {
                                stack.add(e.getProduction().get(0));
                                indexes.add(0);
                            }
                        }
                        index++;
                    } else if (stack.get(index) instanceof SpecialSymbol) {
                        index++;
                    } else {
                        parsingTable.put(new LL1Key(p.getNonterminal(),stack.get(index)),p.getProduction());
                        index = stack.size();
                    }
                }
            }
        }

        System.out.print(g);
        System.out.println(first);
        System.out.println(follow);
        System.out.println(parsingTable);

        return parsingTable;
    }
}
