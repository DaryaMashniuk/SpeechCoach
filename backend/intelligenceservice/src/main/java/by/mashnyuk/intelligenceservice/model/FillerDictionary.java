package by.mashnyuk.intelligenceservice.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class FillerDictionary {
  private static final Map<Language, Set<String>> DICTIONARY = new EnumMap<>(Language.class);

  static {
    DICTIONARY.put(Language.en, Set.of(
            "um", "uh", "erm", "er", "like", "you know", "i mean", "well",
            "so", "right", "actually", "basically", "literally", "sort of",
            "kind of", "okay", "ok", "hmm", "oh", "oh yeah", "you see",
            "stuff", "things", "and stuff", "and things"
    ));

    DICTIONARY.put(Language.ru, Set.of(
            "ну", "типа", "как бы", "вот", "короче", "значит", "собственно",
            "так сказать", "в принципе", "допустим", "на самом деле",
            "понимаешь", "слушай", "это самое", "как его", "блин", "эм",
            "мм", "ну вот", "в общем", "в смысле", "скажем так"
    ));

    DICTIONARY.put(Language.de, Set.of(
            "äh", "ähm", "hm", "also", "halt", "so", "quasi",
            "sozusagen", "eigentlich", "weißt du", "ich mein", "genau",
            "naja", "eben", "ja", "doch"
    ));

    DICTIONARY.put(Language.fr, Set.of(
            "euh", "bah", "ben", "beh", "quoi", "hein", "en fait", "du coup",
            "genre", "tu vois", "tu sais", "alors", "bon", "enfin",
            "voilà", "bref", "quoi hein", "comment dire"
    ));

    DICTIONARY.put(Language.es, Set.of(
            "eh", "este", "pues", "o sea", "bueno", "vale", "a ver",
            "entonces", "como", "en plan", "¿sabes?", "¿vale?", "o sea que",
            "digamos", "mira", "claro"
    ));

    DICTIONARY.put(Language.zh, Set.of(
            "嗯", "呃", "啊", "那个", "就是", "然后", "反正", "对吧",
            "你知道", "怎么说", "其实", "就是说", "所以", "这个", "嗯嗯"
    ));

    DICTIONARY.put(Language.ja, Set.of(
            "えー", "えっと", "あの", "あー", "うーん", "まあ",
            "なんか", "その", "ていうか", "そう", "そうですね",
            "ほら", "じゃない", "だよね", "ええと", "あれ"
    ));

    DICTIONARY.put(Language.pt, Set.of(
            "ah", "eh", "hum", "hã", "então", "tipo", "assim", "quer dizer",
            "pois", "bem", "né", "pronto", "olha", "sei lá", "tá",
            "vamos ver", "de certa forma"
    ));

    DICTIONARY.put(Language.it, Set.of(
            "eh", "ehm", "allora", "cioè", "tipo", "praticamente",
            "diciamo", "insomma", "boh", "dai", "no?", "capito?",
            "guarda", "vabbè"
    ));

    DICTIONARY.put(Language.tr, Set.of(
            "eee", "ıı", "şey", "yani", "hani", "mesela", "hani böyle",
            "işte", "tabii", "değil mi", "bak", "falan", "filan"
    ));

    DICTIONARY.put(Language.ar, Set.of(
            "يعني", "إمم", "آه", "أه", "طيب", "تمام", "شوف", "بص",
            "حلو", "مثلًا", "أكيد", "والله", "بصراحة", "يعني كذا"
    ));

    DICTIONARY.put(Language.pl, Set.of(
            "yyy", "yy", "no", "wiesz", "taki", "jakby", "czyli",
            "w zasadzie", "w sumie", "tego", "ten", "kurczę", "mhm",
            "no bo", "powiedzmy", "że tak powiem"
    ));

    DICTIONARY.put(Language.nl, Set.of(
            "eh", "uh", "dus", "zeg maar", "ja", "nou", "even",
            "eigenlijk", "als het ware", "weet je", "ik bedoel", "kijk",
            "zeg", "dus ja"
    ));

//    DICTIONARY.put(Language., Set.of(
//            "어", "음", "그", "그러니까", "있잖아", "뭐랄까", "그냥", "아니",
//            "맞아", "알지", "그렇지", "음음"
//    ));

    DICTIONARY.put(Language.id, Set.of(
            "eh", "emm", "anu", "kan", "gitu", "jadi", "sebenarnya",
            "kayak", "maksudnya", "ya", "nah", "begitu", "itu lho"
    ));

    DICTIONARY.put(Language.ar, Set.of(
            "يعني", "طيب", "تمام", "آه", "إمم", "والله", "شوف", "بص", "بصراحة"
    ));
  }

  public static Set<String> getForLanguage(Language lang) {
    return DICTIONARY.getOrDefault(lang, DICTIONARY.get(Language.en));
  }
}