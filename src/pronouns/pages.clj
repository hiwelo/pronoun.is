(ns pronouns.pages
  (:require [clojure.string :as s]
            [pronouns.util :as u]
            [hiccup.core :refer :all]))

(defn wrap-pronoun
  [pronoun]
  [:b pronoun])

(defn render-sentence [& content]
  [:p [:span.sentence content]]
)

(defn subject-example
  [subject]
  (render-sentence (wrap-pronoun (s/capitalize subject)) " went to the park."))

(defn object-example
  [object]
  (render-sentence "I went with " (wrap-pronoun object) "."))

(defn posessive-determiner-example
  [subject possessive-determiner]
  (render-sentence (wrap-pronoun (s/capitalize subject)) " brought " (wrap-pronoun possessive-determiner) " frisbee."))

(defn possessive-pronoun-example
  [possessive-pronoun]
  (render-sentence "At least I think it was " (wrap-pronoun possessive-pronoun) "."))

(defn reflexive-example
  [subject reflexive]
  (render-sentence (wrap-pronoun (s/capitalize subject)) " threw the frisbee to " (wrap-pronoun reflexive) "."))

(defn examples-block
  [subject object possessive-determiner possessive-pronoun reflexive]
  [:div {:class "examples"}
   [:p [:h1 "Here are some usage examples for my pronouns:"]]
   (subject-example subject)
   (object-example object)
   (posessive-determiner-example subject possessive-determiner)
   (possessive-pronoun-example possessive-pronoun)
   (reflexive-example subject reflexive)])

(defn about-block []
  [:div {:class "about"}
   [:p "Full usage:"]
   [:p
    [:tt "http://pronoun.is/subject-pronoun/object-pronoun/possessive-determiner/possessive-pronoun/reflexive"]
    " displays examples of your pronouns. If we have a good guess we'll let you use just the first one or two."]
   [:p "Quick examples:"]
   [:p "My name is Thel Seraphim, my " [:a {:href "http://pronoun.is/she"} "pronoun.is/she"] "."]
   [:p "My name is Morgan, my " [:a {:href "http://pronoun.is/ze/zir"} "pronoun.is/ze/zir"] "."]])

(defn contact-block []
  (let [twitter-name (fn [handle] [:a {:href (str "https://www.twitter.com/" handle)} (str "@" handle)])]
  [:div {:class "contact"}
   [:p "Written by " (twitter-name "morganastra") " and " (twitter-name "thelseraphim") ". "
   "Visit the project on " [:a {:href "https://github.com/witch-house/pronoun.is"} "github!"]]]))


(defn format-pronoun-examples
  [subject object possessive-determiner possessive-pronoun reflexive]
  (html
   [:html
    [:head
     [:title "Pronoun examples"]
     [:link {:rel "stylesheet" :href "/pronouns.css"}]]
    [:body
     (examples-block subject object possessive-determiner possessive-pronoun reflexive)
     (about-block)
     (contact-block)]]))


(defn parse-pronouns-with-lookup [pronouns-string pronouns-table]
  (let [inputs (s/split pronouns-string #"/")
        n (count inputs)]
    (if (>= n 5)
      (take 5 inputs)
      (u/table-lookup inputs pronouns-table))))

;; we could choose to display the entire row for the label.
;; currently the first two entries are enough to disambiguate the
;; pronouns -- will that always be true?
(defn make-link [row]
  (let [link (str "/" (s/join "/" row))
        label (str (first row) "/" (first (rest row)))]
    [:li [:a {:href link} label]]))

(defn front [pronouns-table]
  (let [links (map make-link (sort pronouns-table))]
    (html
     [:html
      [:head
       [:title "Main Page"]
       [:link {:rel "stylesheet" :href "/pronouns.css"}]]
      [:body
       [:p "pronoun.is is a www site for showing people how to use pronouns"]
       [:p "here are some pronouns the site knows about:"]
       [:ul links]]])))

(defn not-found []
  (str "We couldn't find those pronouns in our database, please ask us to "
       "add them, or issue a pull request at "
       "https://github.com/witch-house/pronoun.is/blob/master/resources/pronouns.tab"))

(defn pronouns [path pronouns-table]
  (let [pronouns (parse-pronouns-with-lookup path pronouns-table)]
    (if pronouns
      (apply format-pronoun-examples pronouns)
      (not-found))))
