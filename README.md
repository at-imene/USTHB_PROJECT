# USTHB_PROJECT


1. **L’Authentification**

![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 001](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/dcfcc664-3527-4a9c-a822-9633904ed638) ![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 002](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/66621323-cbb4-438d-8ce9-d07a4d06ab68)

















Pour pouvoir utiliser USTHB AR l'utilisateur doit s'authentifier ou créer un nouveau compte en s'inscrivant.

Les informations des utilisateurs seront sauvegardées dans le Firestore.![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 004](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/68919804-c1ee-4cbf-887a-fa23ea0d5497)




2. **Main Functionality**

L'activité main est séparé en trois partie :

1. Le header : contient un avatar avec le nom de l’utilisateur.
2. Main Buttons : qui sont les 3 boutons qui permette de :
   1. Qr code : scanner un code qr et afficher le modèle correspondant.
   2. Map : Afficher la carte en pointant sur la localisation de l’utilisateur.
   3. Logout : quitter l’application.
3. Search bar : filtrer les endroits par nom.
4. CardViews des endroits de l'usthb :
   1. 3d model : afficher le model 3d.
   2. Localisation : récupérer localisation sur la cate.





3. **MAP**


L'utilisateur peut localiser les différents endroits de l'Usthb facilement ainsi qu'obtenir le chemin à traverser pour arriver à l'un de ces endroits depuis son emplacement. 
![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 005](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/6c0bcb23-66f7-431f-9a32-bc518c7ec39e)







4. **Model 3d**
1. L’utilisateur peut afficher le modèle 3d d’un meuble en scannant son code qr.

2. En cliquant sur l’icon 3d model.

![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 006](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/f47a253b-1cca-456e-99a0-1c898fd78edb)


L'activité chargée de scanner les codes Qr



![Aspose Words ebf2cc16-1492-49e9-978f-09293dd86441 007](https://github.com/at-imene/USTHB_PROJECT/assets/78742254/5c15df2e-91f3-4f64-8874-806c2ca822a1)












5. **Les permissions**

Pour que l'application fonctionne correctement, l'utilisateur doit accorder une autorisation à l'application.

- Connection internet.
- Activer la localisation.
- Android version 8.0 ou supérieur.


6. **Database :**

On a choisi de sauvegarder nos modèles dans Firebase Storage et pour l’authentification on a utilisé Firestore.

