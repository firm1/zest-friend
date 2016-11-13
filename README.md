# Zest-Friend, vos vrais amis sur Zeste de Savoir ?

Il s'agit d'un simple outil qui vous permet de déterminer a avec qui est-ce que vous échangez le plus souvent sur la plateforme Zeste de Savoir

## Télécharger l'executable

L'executable se trouve en ligne [ici](https://bintray.com/firm1/maven/download_file?file_path=zest-friend-all-1.0.jar)

Attention pour lancer le programme vous devez posseder une version de java8 au moins.

Pour lancer le programme, ouvrez un terminal et lancer la commande `java -jar zest-friend-all-1.0.jar "login" "mot-de-passe"`

## *Builder* le projet

Attention : vous devez absolument avoir Java8 installé sur votre machine. Pour le savoir il suffit d'ouvrir un terminal et de lancer la commande `java -version`.

Pour compiler le projet, il suffit de :

- cloner le dépot `git clone https://github.com/firm1/zest-friend`
- vous rendre dans le répertoire
- lancer la commande `./gradlew fatJar`

Le jar sera alors disponible dans votre dossier `build/libs/`
