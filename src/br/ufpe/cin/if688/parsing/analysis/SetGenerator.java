package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator {

    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

        if (g == null) throw new NullPointerException("g nao pode ser nula.");

        Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);

        Set<GeneralSymbol> firstTemp; //Conjunto de símbolos temporário
        Collection<Production> rules = g.getProductions(); // regras da gramática
        List<GeneralSymbol> listToDo; //Lista dos símbolos a serem avaliados
        List<Integer> indexes; //Lista dos indexes relacionados àquele símbolo
        int index; //Index que eu estou olhando atualmente na listToDo e indexes

        for (Production e: rules) {
            firstTemp = new HashSet<>();
            listToDo = new ArrayList<>();
            indexes = new ArrayList<>();
            listToDo.add(e.getNonterminal()); //Adicionando o símbolo inicial
            indexes.add(0);
            listToDo.add(e.getProduction().get(0)); //Adicionando o primeiro símbolo da lista de produção
            indexes.add(0);
            index = 1;
            while (!(index >= listToDo.size())) {
                if (listToDo.get(index) instanceof Nonterminal) {
                    if (!first.get(listToDo.get(index)).isEmpty()){ //Checo se existe first do símbolo que estou a visualizar na listToDo
                        firstTemp.addAll(first.get(e.getNonterminal())); //Caso tenha, adiciono todos os símbolos ao meu first temporário
                    } else {
                        for (Production p : rules) {
                            if (p.getNonterminal() == listToDo.get(index)) { //Caso o símbolo de produção (lado esquerdo) seja o que estou olhando agora na listToDo
                                listToDo.add(p.getProduction().get(0)); //Adiciono o primeiro símbolo daquela regra a minha lista
                                indexes.add(0);
                            }
                        }
                    }
                    index++;
                } else {
                    if (!first.get(e.getNonterminal()).isEmpty()){ //Caso meu nãoterminal do lado esquerdo atual tenha first, eu adiciono ele ao meu first temporário.
                        firstTemp.addAll(first.get(e.getNonterminal()));
                    }
                    firstTemp.add(listToDo.get(index)); //Adiciono o símbolo especial ou terminal que eu achei no meu first temporário
                    index++;
                    if (index >= listToDo.size()) { //Caso eu tenha passado por toda listToDo
                        if (firstTemp.contains(SpecialSymbol.EPSILON)) { //Caso meu first temporário contenha EPSILON
                            GeneralSymbol aux;
                            int auxiliary;
                            do { //Removo intens da lista e volto meu index enquanto eu não achar um não terminal
                                aux = listToDo.remove(listToDo.size()-1);
                                auxiliary = indexes.remove(indexes.size()-1);
                                index--;
                            } while (!(aux instanceof Nonterminal));
                            if (aux != e.getNonterminal()) { //Caso eu não tenha voltado até a regra inicial (voltar à regra inicial indica que epsilon faz parte do first)
                                for (Production p : rules) {
                                    if (auxiliary < p.getProduction().size() && p.getProduction().get(auxiliary) == aux) { //Checo se o index é menor que o tmanho da produção
                                        auxiliary++;
                                        aux = p.getNonterminal(); //atribuo à aux o não terminal daquela produção
                                        for (Production h : rules) {
                                            if (h.getNonterminal() == aux) {//procuro por uma produção que tenha aux como não terminal para adicionar aos possiveis firsts
                                                if (auxiliary >= h.getProduction().size()) {
                                                    break;
                                                } else {
                                                    firstTemp.remove(SpecialSymbol.EPSILON);
                                                    listToDo.add(h.getProduction().get(auxiliary));
                                                    indexes.add(auxiliary);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            first.put(e.getNonterminal(),firstTemp);
        }

        return first;

    }


    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {

        if (g == null || first == null)
            throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);

        Set<GeneralSymbol> followTemp;
        Collection<Production> rules = g.getProductions();
        List<Production> listToDo;
        List<Integer> indexes;
        int index;

        for (Production e : rules) {
            followTemp = new HashSet<>();
            listToDo = new ArrayList<>();
            indexes = new ArrayList<>();
            index =0;

            if (g.getStartSymbol()==e.getNonterminal()){
                followTemp.add(SpecialSymbol.EOF); //Caso seja o símbolo inicial adiciono EOF ao follow
            }

            for (Production p : rules) {
                if (p.getProduction().contains(e.getNonterminal())){ //Para cada produção que possui àquele não terminal em questão eu adiciono à listToDo
                    indexes.add(p.getProduction().indexOf(e.getNonterminal()));
                    listToDo.add(p);
                }
            }

            while (!(index >= listToDo.size())) {
                if (indexes.get(index) >= (listToDo.get(index).getProduction().size() -1)) {//Caso o símbolo qu estou olhando seja o último da produção
                    if (!(listToDo.get(index).getProduction().get(indexes.get(index)) instanceof Nonterminal)) {
                        followTemp.add(listToDo.get(index).getProduction().get(indexes.get(index)));//Caso o último símbolo não seja um não terminal eu o adiciono ao follow
                    } else {
                        if ((listToDo.get(index).getProduction().get(indexes.get(index)) != listToDo.get(index).getNonterminal())){//Caso o não terminal que eu eteja tratando não seja o não terminal do lado esquerdo da produção
                            if (follow.get(listToDo.get(index).getNonterminal()).isEmpty()) {
                                for (Production p : rules) { //Caso meu não terminal do lado esquerdo não tenha o follow já calculado eu o adiciono a listToDo
                                    if (p.getProduction().contains(listToDo.get(index).getNonterminal())){
                                        indexes.add(p.getProduction().indexOf(listToDo.get(index).getNonterminal()));
                                        listToDo.add(p);
                                    }
                                }
                            } else { //Caso possua follow, adiciono ao meu follow temporário.
                                followTemp.addAll(follow.get(listToDo.get(index).getNonterminal()));
                            }
                        }
                    }
                } else {//Capturo o próximo símbolo depois do que quero saber o follow e diferencio se é ou não um não terminal
                    GeneralSymbol auxiliary = listToDo.get(index).getProduction().get(indexes.get(index) + 1);
                    if (auxiliary instanceof Nonterminal) {
                        if (first.get(auxiliary).contains(SpecialSymbol.EPSILON)) {//Caso seja um não terminal, checo se possui epsilon e caso tenha procuro o first do próximo símbolo também o adicionando à listToDo
                            if ((indexes.get(index)+2) > (listToDo.get(index).getProduction().size() -1)) {
                                for (Production p : rules) {
                                    if (p.getProduction().contains(listToDo.get(index).getNonterminal())){
                                        indexes.add(p.getProduction().indexOf(listToDo.get(index).getNonterminal()));
                                        listToDo.add(p);
                                    }
                                }
                            } else {
                                indexes.add(indexes.get(index) + 1);
                                listToDo.add(listToDo.get(index));
                            }
                            followTemp.addAll(first.get(auxiliary));
                            followTemp.remove(SpecialSymbol.EPSILON);
                        } else {
                            followTemp.addAll(first.get(auxiliary));
                        }
                    } else {//Caso não seja um não terminal, eu apenas o adiciono ao follow
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
