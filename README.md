# android

Primele 4 laboratoare sunt implementate in aplicatia "MyApplication", iar laboratorul 5 in aplicatia "SettingsApp".

Pentru laboratorul 4 am creat un meniu folosind MenuInflater. In meniu am doua campuri "Settings", care imi intra intr-o noua activitate in care se face o setare de schimbare a temei, folosind SharedPreferences, si "Quit!", care imi inchide aplicatia.
Am creat si un buton "Send message" in MainActivity care, daca este apasat, ne trimite catre o alta acivitate, folosind Intent ce va afisa un mesaj intr-un TextView. Dar inainte de asta, un AlertDialog este afisat pentru a verifica daca chiar dorim sa facem aceasta actiune sau nu.

In aplicatia "SettingsApp", am refacut sharedPreferences din aplicatia "MyApplication", construind un SettingsActivity pentru activitatea de setari. In aceasta am un SwitchPreferenceCompat care imi indica daca textul va fi negru sau daca va fi colorat. Am folosit PreferenceManager.getDefaultSharedPreferences() pentru a prelua informatia din Switch, si astfel textul va fi colorat corespunzator setarilor.
Pentru a stoca intern, am scris intr-un fisier textul din EditText. MainActivity are un buton "login" ce ma trimite catre o alta activitate care afiseaza ceea ce am scris in fisier. Acest lucru l-am realizat cu ajutorul claselor FileOutputStream/FileInputStream.
