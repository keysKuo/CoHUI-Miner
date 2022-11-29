import java.util.*;
import java.util.stream.Stream;
import java.io.*;

public class Algorithm {
    private ArrayList<ArrayList<String>> Trans;
    private ArrayList<ArrayList<String>> Pq;
    private ArrayList<ArrayList<String>> Util;
    private ArrayList<Integer> uT;
    
    private ArrayList<String> Ikeep;
    private ArrayList<String> profit;
    
    public Algorithm() {
        Trans = new ArrayList<>();
        Pq = new ArrayList<>();
        Util = new ArrayList<>();
        uT = new ArrayList<>();
        Ikeep = new ArrayList<>();
        profit = new ArrayList<>();
    }

    public void readData(String path1, String path2) {
        try {
            File file = new File(path1);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\|");
                Trans.add(new ArrayList<String>(Arrays.asList(parts[0].split(","))));
                Pq.add(new ArrayList<String>(Arrays.asList(parts[1].split(","))));
                Util.add(new ArrayList<String>(Arrays.asList(parts[2].split(","))));
                
                uT.add(Integer.parseInt(parts[3]));
            }
            sc.close();

            file = new File(path2);
            sc = new Scanner(file);
            String[] K = sc.nextLine().split(",");
            for (String k: K ) {
                Ikeep.add(k);
            }
            
            String[] P = sc.nextLine().split(",");
            for (String p: P) {
                profit.add(p);
            }
            
            sc.close();
            
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private boolean isSubset(ArrayList<String> parent, ArrayList<String> child) {
        Set<String> p = new HashSet<>(parent);
        return p.containsAll(child);
    }

    public HashMap<ArrayList<String>, Integer> SUP_Item(ArrayList<ArrayList<String>> itemSets) {
        HashMap<ArrayList<String>, Integer> result = new HashMap<>();

        for (ArrayList<String> iSet: itemSets) {
            int counter = 0;
            for (ArrayList<String> tr: Trans) {
                if (isSubset(tr, iSet)) {
                    counter++;
                }
                result.put(iSet, counter);
            }
        }
        return result;
    }

    public int SUP_Items2(ArrayList<String> iSet) {
        int counter = 0;
        for (ArrayList<String> tr: Trans) {
            if (isSubset(tr, iSet)) {
                counter++;
            }
        }
        return counter;
    }

    public ArrayList<Integer> TWU_Items(ArrayList<ArrayList<String>> itemSets) {
        ArrayList<Integer> result = new ArrayList<>();
        
        for (ArrayList<String> iset: itemSets) {
            int s = 0;
            for (int i = 0; i < Trans.size(); i++) {
                if (isSubset(Trans.get(i), iset)) {
                    s += uT.get(i);
                }
            }
            result.add(s);
        }
        return result;
    }

    public int TWU_Items2(ArrayList<String> iSet) {
        int s = 0;
        for (int i = 0; i < Trans.size(); i++) {
            if (isSubset(Trans.get(i), iSet)) {
                s += uT.get(i);
            }
        }
        return s;
    }

    public HashMap<String, Integer> U() {
        HashMap<String, Integer> result = new HashMap<>();
        
        for (int i = 0; i < Trans.size(); i++) {
            for (int j = 0; j < Trans.get(i).size(); j++) {
                String key = Trans.get(i).get(j);
                if (!result.containsKey(key)) {
                    result.put(key, Integer.parseInt(Util.get(i).get(j)));
                }
                else {
                    result.put(key, result.get(key) + Integer.parseInt(Util.get(i).get(j)));
                }
            }   
        }
        return result;
    }

    public boolean isGreater(String x, ArrayList<String> X) {
        ArrayList<String> t1 = new ArrayList<>();
        t1.add(x);
        int a = SUP_Items2(t1);
        int counter = 0;

        for (String element: X) {
            ArrayList<String> t2 = new ArrayList<>();
            t2.add(element);
            int b = SUP_Items2(t2);
            if (a < b || (a == b && (int)(x.charAt(0)) < (int)(element.charAt(0)))) {
                counter++;
            }
        }
        if (counter == X.size()) {
            return true;
        }
        return false;
    }

    public boolean isLess(String x, ArrayList<String> X) {
        ArrayList<String> t1 = new ArrayList<>();
        t1.add(x);
        int a = SUP_Items2(t1);
        int counter = 0;

        for (String element: X) {
            ArrayList<String> t2 = new ArrayList<>();
            t2.add(element);
            int b = SUP_Items2(t2);
            if (a > b || (a == b && (int)(x.charAt(0)) > (int)(element.charAt(0)))) {
                counter++;
            }
        }
        if (counter == X.size()) {
            return true;
        }
        return false;
    }


    public void CoHUI_Miner(double minCor, int minUtility, ArrayList<ArrayList<String>> CoHUIs) {
        ArrayList<String> thr0w = new ArrayList<>();
        
        ArrayList<String> clone = (ArrayList<String>) Ikeep.clone();
        
        for (int i = 0; i < clone.size(); i++) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(clone.get(i));
            if (TWU_Items2(temp) < minUtility) {
                thr0w.add(Ikeep.get(i));
                Ikeep.remove(i);
            }
        }

        for (int i = 0; i < Trans.size(); i++) {
            Set<String> temp = new HashSet<>(Trans.get(i));
            for (String th: thr0w) {
                if (temp.contains(th)) {
                    int j = Trans.get(i).indexOf(th);
                    Trans.get(i).remove(j);
                    Pq.get(i).remove(j);
                    uT.set(i, uT.get(i) - Integer.parseInt(Util.get(i).remove(j)));
                }
            }

            SORT(Trans.get(i), Pq.get(i), Util.get(i));
        }

        HashMap<String, Integer> U = U();
        ArrayList<ArrayList<String>> itemSets = new ArrayList<>();
        Cardinality(Ikeep, 1, itemSets, new ArrayList<String>());
        
        for (String X: Ikeep) {
            ArrayList<String> XList = new ArrayList<>();
            XList.add(X);
            if (U.get(X) >= minUtility) {    
                CoHUIs.add(XList);
                System.out.print(XList + " - ");
                System.out.println(U.get(X));
            }

            int RemainU = 0;
            ArrayList<Integer> pru_Tx = new ArrayList<>();
            
            ArrayList<Integer> uT_Tx = new ArrayList<>();
            ArrayList<ArrayList<Integer>> utils = new ArrayList<>();
            ArrayList<ArrayList<String>> dbProjectX = new ArrayList<>();

            for (int i = 0; i < Trans.size(); i++) {
                int j = 0;
                int uTemp = uT.get(i);

                while (j < Trans.get(i).size() && isGreater(Trans.get(i).get(j), XList)) {
                    uTemp -= Integer.parseInt(Util.get(i).get(j));
                    j++;
                }

                if (j == Trans.get(i).size() || isLess(Trans.get(i).get(j), XList)) {
                    continue;
                }

                else if (j < Trans.get(i).size()) {
                    ArrayList<Integer> util_ = new ArrayList<>();
                    ArrayList<String> Tx = new ArrayList<>();
                    for (int k = j+1; k < Trans.get(i).size(); k++) {
                        Tx.add(Trans.get(i).get(k));
                        util_.add(Integer.parseInt(Util.get(i).get(k)));
                    }
                    utils.add(util_);
                    pru_Tx.add(Integer.parseInt(Util.get(i).get(j)));
                    uT_Tx.add(uTemp);
                    dbProjectX.add(Tx);
                    RemainU += uTemp;
                }
            }

            SearchCoHUI(XList, U.get(X), RemainU, dbProjectX, 2, pru_Tx, utils, minUtility, minCor, uT_Tx, CoHUIs);
        }
        
        
    }

    private void SearchCoHUI(ArrayList<String> X, int Ux, int RUx, ArrayList<ArrayList<String>> dbProjectX, int k, ArrayList<Integer> pru_Tx, ArrayList<ArrayList<Integer>> utils, int minUtility, double minCor, ArrayList<Integer> uT_Tx, ArrayList<ArrayList<String>> CoHUIs) {
        if (k == Ikeep.size())  return;
        ArrayList<ArrayList<String>> itemSets = new ArrayList<>();
        Cardinality(Ikeep, k, itemSets, new ArrayList<>());
       
        for (ArrayList<String> iSet: itemSets) {
            if (isExtends(X, iSet, k)) {
                
                ArrayList<String> lastItem = (ArrayList<String>) iSet.clone();
                // System.out.println(lastItem);

                ArrayList<String> Xx = new ArrayList<>(X);
                if(!X.equals(lastItem)) {
                    for (String item: lastItem) {
                        if (!Xx.contains(item)) {
                            Xx.add(item);
                        }
                    }
                }
                

                ArrayList<ArrayList<String>> newdbProjectX = new ArrayList<>();
                ArrayList<Integer> newuT_Tx = new ArrayList<>();
                ArrayList<Integer> newpru_TX = new ArrayList<>();
                ArrayList<ArrayList<Integer>> newutils = new ArrayList<>();
                int RUx_ = 0;
                int SUPx_ = 0;
                int Ux_ = Ux;
                int ULA = Ux + RUx;

                for (int i = 0; i < dbProjectX.size(); i++) {
                    int j = 0;
                    int uTemp = uT_Tx.get(i);

                    while (j < dbProjectX.get(i).size() && isGreater(dbProjectX.get(i).get(j), lastItem)) {
                        uTemp -= utils.get(i).get(j);
                        j++;
                    }

                    if (j == dbProjectX.get(i).size() || isLess(dbProjectX.get(i).get(j), lastItem)) {
                        Ux_ -= pru_Tx.get(i);
                        ULA -= (pru_Tx.get(i) + uT_Tx.get(i));

                        if (ULA < minUtility) {
                            return;
                        }
                        continue;
                    }
                    else {
                        Ux_ += utils.get(i).get(j);
                        SUPx_++;

                        if (j < dbProjectX.get(i).size()) {
                            ArrayList<Integer> util_ = new ArrayList<>();
                            ArrayList<String> Tx = new ArrayList<>();
                            for (int h = j+1; h < dbProjectX.get(i).size(); h++) {
                                Tx.add(dbProjectX.get(i).get(h));
                                
                                util_.add(utils.get(i).get(h));
                            }
                            newdbProjectX.add(Tx);
                            newutils.add(util_);
                            newpru_TX.add(pru_Tx.get(i) + utils.get(i).get(j));
                            newuT_Tx.add(uTemp);
                            RUx_ += uTemp;
                        }
                    }
                }
                
                if (SUPx_ > 0) {
                    if (Kulc(Xx) >= minCor) {
                        if (Ux_ >= minUtility) {
                            CoHUIs.add(Xx);
                            System.out.print(Xx + " - ");
                            System.out.println(Ux_);
                        }

                        if ((Ux_ + RUx_) >= minUtility) {
                            SearchCoHUI(Xx, Ux_, RUx_, newdbProjectX, k+1, newpru_TX, newutils, minUtility, minCor, newuT_Tx, CoHUIs);
                        }
                    }    
                }
            }
        }
    }

    private double Kulc(ArrayList<String> X_) {
        double counter = 0;
        for (ArrayList<String> T: Trans) {
            if (isSubset(T, X_)) {
                counter++;
            }
        }

        double result = 0;
        for (String x: X_) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(x);
            result += (double) (counter / SUP_Items2(temp));
        }
        
        return (double) result / X_.size();
    }

    private boolean isExtends(ArrayList<String> s1, ArrayList<String> s2, int k) {
        if (k == 1) return s1.equals(s2);
        
        for (int i = 0; i < k - 1; i++) {
            if (!s1.get(i).equals(s2.get(i))) {
                return false;
            }
        }
        return true;       
    }

    private int charToInt(String s) {
        return (int)(s.charAt(0));
    }

    private String intToChar(int n) {
        return "" + (char) n;
    }

    private void Cardinality(ArrayList<String> S, int k, ArrayList<ArrayList<String>> result, ArrayList<String> x) {
        if (x.size() < k)  {
            int a = (x.size() == 0) ? charToInt(S.get(0)) : charToInt(x.get(x.size() - 1)) + 1;
            int b = charToInt(S.get(S.size() - 1)) + 1;
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i = a; i < b; i++) {
                candidates.add(i);
            }

            for (Integer c: candidates) {
                ArrayList<String> t = (ArrayList<String>) x.clone();
                t.add(intToChar(c));
                
                if (t.size() == k) {
                    result.add(t);
                }
                Cardinality(S, k, result, t);
            }
        }
    }

    private void SORT(ArrayList<String> trans, ArrayList<String> pq, ArrayList<String> utils) {
        int n = trans.size();
        for (int i = 0; i < n - 1; i++) {
            ArrayList<String> a = new ArrayList<>();
            
            for (int j = i+1; j < n; j++) {
                ArrayList<String> b = new ArrayList<>();
                a.add(trans.get(i));
                b.add(trans.get(j));
                if (SUP_Items2(a) > SUP_Items2(b)) {
                    Collections.swap(trans, i, j);
                    Collections.swap(pq, i, j);
                    Collections.swap(utils, i, j);
                }

                else if (SUP_Items2(a) == SUP_Items2(b)) {
                    if (charToInt(trans.get(i)) > charToInt(trans.get(j))) {
                        Collections.swap(trans, i, j);
                        Collections.swap(pq, i, j);
                        Collections.swap(utils, i, j);
                    }
                }
                
            } 
        }
    }



    public void show() {
        for (int i = 0; i < Trans.size(); i++) {
            String trans = "";
            String pq = "";
            String utils = "";
            for (int j = 0; j < Trans.get(i).size(); j++) {
                trans += Trans.get(i).get(j) + " ";
                pq += Pq.get(i).get(j) + " ";
                utils += Util.get(i).get(j) + " ";
            }
            System.out.println(trans + " | " + pq + " | " + utils + " | " + uT.get(i).toString());
        }
    }

    public static void main(String[] args) {
        Algorithm Al = new Algorithm();
        Al.readData("data.txt", "profit.txt");
        // Al.show();
        ArrayList<ArrayList<String>> CoHUIS = new ArrayList<>();
        Al.CoHUI_Miner(0.52,70, CoHUIS);
        //Al.show();
    }
}