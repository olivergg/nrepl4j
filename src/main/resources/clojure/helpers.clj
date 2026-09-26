(ns io.olivergg.nrepl4j.helpers
  "Generic, framework-independent REPL helpers: reflection on private fields and
   deproxying AOP wrappers. See spring.clj / cdi.clj for bean lookup, loaded
   separately since they need Spring/CDI on the target app's classpath."
  (:require [clojure.string :as str]))

(defn clearns
  "Unmaps every var in the current namespace - a clean slate for REPL experiments."
  []
  (run! #(ns-unmap *ns* %) (keys (ns-interns *ns*))))

(defn- climb-supers
  "Applies f to klass, then to each superclass in turn, until it returns non-nil."
  [^Class klass f]
  (when klass
    (or (f klass) (recur (.getSuperclass klass) f))))

(defn private-field
  "The accessible java.lang.reflect.Field named field-name on obj's class,
   walking up superclasses until found."
  [obj field-name]
  (let [field (or (climb-supers (.getClass obj)
                                 #(try (.getDeclaredField % (name field-name))
                                       (catch NoSuchFieldException _ nil)))
                   (throw (NoSuchFieldException. (str field-name))))]
    (doto field (.setAccessible true))))

(defn get-field-val
  "Reads a private/protected field by name via reflection."
  [obj field-name]
  (.get (private-field obj field-name) obj))

(defn set-field-val!
  "Writes a private/protected field by name via reflection. Returns value."
  [obj field-name value]
  (.set (private-field obj field-name) obj value)
  value)

(defn unproxy
  "Unwraps a Spring/CGLIB-style AOP proxy (getTargetSource().getTarget());
   returns obj unchanged if it isn't a proxy."
  [obj]
  (try (-> obj .getTargetSource .getTarget) (catch Exception _ obj)))

(defn to-map
  "A Java object's getters as a Clojure map, for quick REPL inspection."
  [obj]
  (bean obj))

(defn call-method
  "Invokes a private/protected method by name via reflection, walking up superclasses
   until a method matching args' runtime types is found. Doesn't match overloads that
   expect a supertype/interface of an arg's actual class."
  [obj method-name & args]
  (let [arg-classes (into-array Class (map #(.getClass ^Object %) args))
        method (or (climb-supers (.getClass obj)
                                  #(try (.getDeclaredMethod % (name method-name) arg-classes)
                                        (catch NoSuchMethodException _ nil)))
                   (throw (NoSuchMethodException. (str method-name))))]
    (.invoke (doto method (.setAccessible true)) obj (into-array Object args))))

(defn thread-dump
  "A formatted dump of all live JVM threads and their stack traces, deadlocks included."
  []
  (let [bean (java.lang.management.ManagementFactory/getThreadMXBean)
        deadlocked (set (.findDeadlockedThreads bean))]
    (->> (.dumpAllThreads bean true true)
         (map (fn [info]
                (str (.toString info)
                     (when (contains? deadlocked (.getThreadId info)) " [DEADLOCKED]"))))
         (str/join "\n"))))
