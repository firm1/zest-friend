package com.zestedesavoir.zestfriend;

import java.util.*;

import static java.lang.System.exit;

public class Main {
    public static int limit = 10;
    public static Map<Member, Integer> COUNT_MY_POSTS_READ_BY_MEMBER = new HashMap<>();
    public static Map<Member, Integer> SIZE_MY_POSTS_READ_BY_MEMBER = new HashMap<>();
    public static Map<Member, Integer> COUNT_MEMBER_POSTS_READ_BY_ME = new HashMap<>();
    public static Map<Member, Integer> SIZE_MEMBER_POSTS_READ_BY_ME = new HashMap<>();

    public static void refreshRelation (Api api, Member me){
        api.getPrivateMessages().stream().forEach(m -> {
            List<Post> posts = api.getPrivatePosts(m);
            List<Member> participants = new ArrayList<>();
            participants.addAll(m.getParticipants());
            participants.add(m.getAuthor());
            posts.stream().forEach(p -> {
                // System.out.println("Topic : "+m.getTitle()+" | ["+p.getId()+"] - "+p.getAuthor().getUsername());

                if(p.getAuthor().equals(me)) { // if i look my post
                    m.getParticipants().stream()
                            .filter(part -> ! part.equals(me))
                            .forEach(part -> {
                        if (COUNT_MY_POSTS_READ_BY_MEMBER.containsKey(part)) { // if participant exist in dict
                            COUNT_MY_POSTS_READ_BY_MEMBER.put(part, COUNT_MY_POSTS_READ_BY_MEMBER.get(part).intValue() + 1);
                            SIZE_MY_POSTS_READ_BY_MEMBER.put(part, SIZE_MY_POSTS_READ_BY_MEMBER.get(part).intValue() + countWords(p.getText()));
                        } else { // participant doesn't exist
                            COUNT_MY_POSTS_READ_BY_MEMBER.put(part, 1);
                            SIZE_MY_POSTS_READ_BY_MEMBER.put(part, countWords(p.getText()));
                        }
                    });
                } else { // posts of others persons
                    if (COUNT_MEMBER_POSTS_READ_BY_ME.containsKey(p.getAuthor())) { // if author exist in dict
                        COUNT_MEMBER_POSTS_READ_BY_ME.put(p.getAuthor(), COUNT_MEMBER_POSTS_READ_BY_ME.get(p.getAuthor()) +1);
                        SIZE_MEMBER_POSTS_READ_BY_ME.put(p.getAuthor(), COUNT_MEMBER_POSTS_READ_BY_ME.get(p.getAuthor()) +countWords(p.getText()));
                    } else { // participant doesn't exist
                        COUNT_MEMBER_POSTS_READ_BY_ME.put(p.getAuthor(), 1);
                        SIZE_MEMBER_POSTS_READ_BY_ME.put(p.getAuthor(), countWords(p.getText()));
                    }
                }
            });

        });

        COUNT_MY_POSTS_READ_BY_MEMBER = sortMaps(COUNT_MY_POSTS_READ_BY_MEMBER);
        COUNT_MEMBER_POSTS_READ_BY_ME = sortMaps(COUNT_MEMBER_POSTS_READ_BY_ME);
        SIZE_MY_POSTS_READ_BY_MEMBER = sortMaps(SIZE_MY_POSTS_READ_BY_MEMBER);
        SIZE_MEMBER_POSTS_READ_BY_ME = sortMaps(SIZE_MEMBER_POSTS_READ_BY_ME);
    }

    private static Map<Member, Integer> sortMaps(Map<Member, Integer> unsortMap) {
        Map<Member, Integer> result = new LinkedHashMap<>();

        unsortMap.entrySet().stream()
                .sorted(Map.Entry.<Member, Integer>comparingByValue().reversed())
                .limit(limit)
                .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

        return result;
    }

    private static String cleanLine(String line) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c < 128 && Character.isLetter(c)) {
                buffer.append(c);
            } else {
                buffer.append(' ');
            }
        }
        return buffer.toString().toLowerCase();
    }

    private static int countWords(String text) {
        text = cleanLine(text);
        Integer currentCount = 0;
        StringTokenizer stringTokenizer = new StringTokenizer(text, " ");
        while (stringTokenizer.hasMoreElements()) {
            String word = stringTokenizer.nextToken();

            if(word.length()>0) {
                currentCount++;
            }
        }
        return currentCount;

    }

    public static void displayResults(String member) {
        String[] header1 = {"Membre", "Nombre de post"};
        String[] header2 = {"Membre", "Nombre de mots"};
        String flip1 = FlipTable.of(header1, mapToArray(COUNT_MY_POSTS_READ_BY_MEMBER));
        String flip2 = FlipTable.of(header2, mapToArray(SIZE_MY_POSTS_READ_BY_MEMBER));
        String flip3 = FlipTable.of(header1, mapToArray(COUNT_MEMBER_POSTS_READ_BY_ME));
        String flip4 = FlipTable.of(header2, mapToArray(SIZE_MEMBER_POSTS_READ_BY_ME));

        System.out.println("/!\\ TOP "+limit+" des membres qui écoutent le plus "+member+" en privé (en NOMBRE de message) /!\\ \n");
        System.out.println(flip1+"\n");
        System.out.println("/!\\ TOP "+limit+" des membres qui écoutent le plus "+member+" en privé (en TAILLE de message) /!\\ \n");
        System.out.println(flip2+"\n");
        System.out.println("/!\\ TOP "+limit+" des membres que "+member+" à le plus écouté en privé (en NOMBRE de message) /!\\ \n");
        System.out.println(flip3+"\n");
        System.out.println("/!\\ TOP "+limit+" des membres que "+member+" à le plus écouté en privé (en TAILLE de message) /!\\ \n");
        System.out.println(flip4+"\n");

    }

    private static String[][] mapToArray(Map<Member, Integer> map) {

        String[][] data = new String[map.size()][];
        int ii =0;
        for(Map.Entry<Member,Integer> entry : map.entrySet()){
            data[ii++] = new String[] { entry.getKey().getUsername(), entry.getValue()+"" };
        }
        return data;
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Merci de renseigner votre nom d'utilisateur suivi de votre mot de passe comme ceci :");
                System.out.println("java -jar zest-friend-all-1.0.jar \"login\" \"mot-de-passe\"");
            exit(1);
        }

        String user = args[0];
        String password = args[1];
        String clientId = "";
        String clientSecret = "";

        if((! clientId.equals("")) && (! clientSecret.equals(""))) {
            System.out.println("A la recherche des informations sur "+args[0]+"\n");
            Api api = new Api(clientId, clientSecret);

            if (api.authenticate(user, password)) {
                refreshRelation(api, api.getMemberFromId(api.getIdFromUsername(user)));
                displayResults(user);
            } else {
                System.out.println("Désolé, mais vous n'arrivez pas à vous authentifier. Vérifiez votre connexion, votre login et votre mot de passe.");
            }
        } else {
            System.out.println("Vous devez renseigner votre clientId et client Secret dans le gradle.properties");
        }

    }
}
