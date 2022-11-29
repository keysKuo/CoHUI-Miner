from copy import deepcopy

def swap(arr, i, j):
    arr[i], arr[j] = arr[j], arr[i]

def Cardinality(S,r,result,x):
    if len(x) < r:
        a = ord(S[0]) if len(x) == 0 else ord(x[-1]) + 1
        b = ord(S[-1]) + 1
        candidates = list(range(a,b))
        
        for c in candidates:
            t = deepcopy(x)
            t.append(chr(c))
            
            if len(t) == r:
                result.append(t)
            Cardinality(S,r,result,t)

def isExtends(s1, s2, k):
    if k == 1:
        return s1 == s2

    for i in range(k-1):
        if s1[i] != s2[i]:
            return False
    
    return True


