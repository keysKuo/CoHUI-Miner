from functions import *

root = '/Users/nkeyskuo/Documents/WorkSpace/Algorithm/Final/'

class Algorithm:
    def __init__(self):
        self.Trans = [] # Transaction 
        self.Pq = [] # Purchase Quantity
        self.Util = [] # Utility
        self.uT = [] # Transaction Utility

        self.Ikeep = [] # [a,b,c,d,e,...] - all of single item
        self.profit = [] # [4,3,1,2,3,...] = - profit of single item

    def readData(self, path1, path2):
        # input: path1 - string, path2: string
        # output: Trans, Pq, Util, uT, Ikeep, profit
        file = open(path1, 'r')
        docx = file.read().split('\n')
        for row in docx:
            r = row.split('|')
            self.Trans.append(r[0].split(','))
            self.Pq.append([int(i) for i in r[1].split(',')])
            self.Util.append([int(i) for i in r[2].split(',')])
            self.uT.append([int(r[3])])
        
        file = open(path2, 'r')
        docx = file.read().split('\n')
        self.Ikeep = docx[0].split(',')
        self.profit = [int(i) for i in docx[1].split(',')]

    # input: itemSets - list of ItemSet
    # output: occurrences of every itemSet
    def SUP_Items(self, itemSets):
        result = {}

        for iSet in itemSets:
            counter = 0
            for tr in self.Trans:
                if all(element in tr for element in iSet):
                    counter += 1
            result[str(iSet)] = counter 
        return result


    # input: iSet - single ItemSet
    # output: occurrence the iSet
    def SUP_Items2(self, iSet):
        counter = 0
        for tr in self.Trans:
            if all(element in tr for element in iSet):
                    counter += 1
        return counter

    
    # input: itemSets - list of ItemSet
    # output: Transaction Utilities of every itemSet
    def TWU_Items(self, itemSets):
        result = []
        
        for iSet in itemSets:
            s = 0 
            for i in range(len(self.Trans)):
                if all(element in self.Trans[i] for element in iSet):
                    s += self.uT[i][0]
            result.append(s)

        return result

    # input: iSet - single of ItemSet
    # output: Transaction Utility of the iSet
    def TWU_Items2(self, iSet):
        s = 0
        for i in range(len(self.Trans)):
            if all(element in self.Trans[i] for element in iSet):
                s += self.uT[i][0]
        return s

    # input: List of transactions
    # output: Sum of Utilitities of every elements
    def U(self):
        U = {}
        for i in range(len(self.Trans)):
            for j in range(len(self.Trans[i])):
                if U.get(self.Trans[i][j]) is None:
                    U[self.Trans[i][j]] = self.Util[i][j]
                else:
                    U[self.Trans[i][j]] += self.Util[i][j]
        
        return U

    # input: x - an element, X - a set of elements
    # output: return true if X > x else false
    def isGreater(self, x, X):
        a = self.SUP_Items2(x)
        counter = 0
        for element in X:
            b = self.SUP_Items2(element)
            if a < b or (a == b and ord(x) < ord(element)):
                counter += 1
        if counter == len(X):
            return True
        return False

    # input: x - an element, X - a set of elements
    # output: return true if X < x else false
    def isLess(self, x, X):
        a = self.SUP_Items2(x)
        counter = 0
        for element in X:
            b = self.SUP_Items2(element)
            if a > b or (a == b and ord(x) > ord(element)):
                counter += 1
        if counter == len(X):
            return True
        return False

    # input: database, minCor , minUtility
    # output: CoHuis - set of all correlated HUIs
    def CoHUI_Miner(self, minCor, minUtility, CoHUIs):
        throw = []
        
        for i in range(len(deepcopy(self.Ikeep))):
            if self.TWU_Items2(self.Ikeep[i]) < minUtility:
                throw.append(self.Ikeep[i])
                self.Ikeep.pop(i)
        
        for i in range(len(self.Trans)):
            for th in throw:
                if th in self.Trans[i]:
                    j = self.Trans[i].index(th)
                    self.Trans[i].remove(th)
                    self.Pq[i].pop(j)
                    self.uT[i][0] -= self.Util[i].pop(j)
        
            self.SORT(self.Trans[i], self.Pq[i], self.Util[i])

        U = self.U()
        
        itemSets = []
        Cardinality(self.Ikeep, 1, itemSets, [])

        for X in self.Ikeep:
            if U[X] >= minUtility:
                CoHUIs.append([X])
                print([X], end=' - ')
                print(U[X])
                
            RemainU = 0
            pru_Tx = []
            Tx = 0
            uT_Tx = []
            utils = []
            dbProjectX = []
                
            for i in range(len(self.Trans)):
                j = 0
                uTemp = self.uT[i][0]
                
                while j < len(self.Trans[i]) and self.isGreater(self.Trans[i][j], X):
                    uTemp -= self.Util[i][j]
                    j += 1
                
                if j == len(self.Trans[i]) or self.isLess(self.Trans[i][j], X):
                    continue
                elif j < len(self.Trans[i]):
                    Tx = self.Trans[i][j+1:]
                    pru_Tx.append(self.Util[i][j])
                    uT_Tx.append(uTemp)
                    utils.append(self.Util[i][j+1:])
                    dbProjectX.append(Tx)
                    RemainU += uTemp
            
            self.SearchCoHUI([X], U[X], RemainU, dbProjectX, 2, pru_Tx, utils, minUtility, minCor, uT_Tx, CoHUIs)
        
        

    # input: X : prefix itemset; U (X ): utility of X , RU (X ): the remain utility of X , dbProjectX : projected database with X prefix; k : length of items set X .
    # output: itemsets are CoHUIs with X prefix
    def SearchCoHUI(self, X, Ux, RUx, dbProjectX, k, pru_Tx, utils, minUtility,  minCor, uT_Tx, CoHUIs):
        if k == len(self.Ikeep):
            return 
        itemSets = []
        Cardinality(self.Ikeep, k, itemSets, [])
        
        for iSet in itemSets:
            if isExtends(X, iSet, k):
                lastItem = iSet
                
                Xx = X if (lastItem in X or lastItem == X) else X + list(set(lastItem) - set(X))
               
                newdbProjectX = []
                newuT_Tx = []
                newpru_TX = []
                newutils = []
                RUx_ = 0
                SUPx_ = 0
                Ux_ = Ux
                ULA = Ux + RUx

                for i in range(len(dbProjectX)):
                    j = 0
                    uTemp = uT_Tx[i]

                    while j < len(dbProjectX[i]) and self.isGreater(dbProjectX[i][j], lastItem):
                        uTemp -= utils[i][j]
                        j += 1
                    if j == len(dbProjectX[i]) or self.isLess(dbProjectX[i][j], lastItem):
                        Ux_ -= pru_Tx[i]
                        ULA -= (pru_Tx[i] + uT_Tx[i])

                        if ULA < minUtility:
                            return
                        continue
                    else:
                        Ux_ += utils[i][j]
                        SUPx_ += 1

                        if j < len(dbProjectX[i]):
                            newdbProjectX.append(dbProjectX[i][j+1:])
                            newpru_TX.append(pru_Tx[i] + utils[i][j])
                            newuT_Tx.append(uTemp)
                            newutils.append(utils[i][j+1:])
                            RUx_ += uTemp

                if SUPx_ > 0:
                    if self.Kulc(Xx) >= minCor:
                        if Ux_ >= minUtility:
                            CoHUIs.append(Xx)
                            print(Xx, end=' - ')
                            print(Ux_)

                        if Ux_ + RUx_ >= minUtility:
                            self.SearchCoHUI(Xx, Ux_, RUx_, newdbProjectX, k+1, newpru_TX, newutils, minUtility, minCor, newuT_Tx, CoHUIs)
                
    # input: X_ - an ItemSet
    # output: 
    def Kulc(self, X_):
        counter = 0
        for T in self.Trans:
            if all(element in T for element in X_):
                counter += 1
        
        res = 0
        for x in X_:
            res += (counter / self.SUP_Items2(x))

        return res / len(X_)
        


    def show(self):
        combo = []
        Cardinality(self.Ikeep,3, combo, [])
        print(combo)
        # self.CoHUI_Miner(1, 70)
        # for i in range(len(self.Trans)):
            
        #     print(self.Trans[i], end=' | ')
        #     print(self.Pq[i], end=' | ')
        #     print(self.Util[i], end=' | ')
        #     print(self.uT[i])

    # input : database
    # output: ascending sort according to SUP
    def SORT(self, trans, pq, utils):
        n = len(trans)
        for i in range(0,n-1):
            for j in range(i+1, n):
                if self.SUP_Items2(trans[i]) > self.SUP_Items2(trans[j]):
                    swap(trans,i,j)
                    swap(pq,i,j)
                    swap(utils,i,j)
                elif self.SUP_Items2(trans[i]) == self.SUP_Items2(trans[j]):
                    if ord(trans[i]) > ord(trans[j]):
                        swap(trans,i,j)
                        swap(pq,i,j)
                        swap(utils,i,j)
                

if __name__ == '__main__':
    Al = Algorithm()
    Al.readData(root + 'data.txt', root + 'profit.txt')
    CoHUIs = []
    
    Al.CoHUI_Miner(0.52,70, CoHUIs)
    
    # print(CoHUIs)
    
    
        