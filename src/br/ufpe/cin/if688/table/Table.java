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
        List<GeneralSymbol> listToDo;
        List<Integer> indexes;
        int index;

        for (Production p : rules) {
            if (p.getProduction().contains(SpecialSymbol.EPSILON)){//Caso especial caso a regra produza epsilon eu adiciono-a para todos os simbolos de follow
                fstflwTemp = follow.get(p.getNonterminal());
                for (GeneralSymbol s : fstflwTemp) {
                    parsingTable.put(new LL1Key(p.getNonterminal(),s),p.getProduction());
                }
            } else {
                listToDo = new ArrayList<>();
                indexes = new ArrayList<>();
                listToDo.add(p.getProduction().get(0));
                indexes.add(0);
                index = 0;
                while (!(index >= listToDo.size())) {//Caso do cálculo do first sem a existência do epsilon
                    if (listToDo.get(index) instanceof Nonterminal) {
                        for (Production e : rules) {
                            if (e.getNonterminal() == listToDo.get(index)) {
                                listToDo.add(e.getProduction().get(0));
                                indexes.add(0);
                            }
                        }
                        index++;
                    } else if (listToDo.get(index) instanceof SpecialSymbol) {
                        index++;
                    } else {
                        parsingTable.put(new LL1Key(p.getNonterminal(),listToDo.get(index)),p.getProduction());
                        index = listToDo.size();
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
