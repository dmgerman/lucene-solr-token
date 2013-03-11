begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharacterUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * HunspellStemmer uses the affix rules declared in the HunspellDictionary to generate one or more stems for a word.  It  * conforms to the algorithm in the original hunspell algorithm, including recursive suffix stripping.  */
end_comment

begin_class
DECL|class|HunspellStemmer
specifier|public
class|class
name|HunspellStemmer
block|{
DECL|field|RECURSION_CAP
specifier|private
specifier|static
specifier|final
name|int
name|RECURSION_CAP
init|=
literal|2
decl_stmt|;
DECL|field|dictionary
specifier|private
specifier|final
name|HunspellDictionary
name|dictionary
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|StringBuilder
name|segment
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|charUtils
specifier|private
name|CharacterUtils
name|charUtils
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
decl_stmt|;
comment|/**    * Constructs a new HunspellStemmer which will use the provided HunspellDictionary to create its stems    *    * @param dictionary HunspellDictionary that will be used to create the stems    */
DECL|method|HunspellStemmer
specifier|public
name|HunspellStemmer
parameter_list|(
name|HunspellDictionary
name|dictionary
parameter_list|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
block|}
comment|/**    * Find the stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|String
name|word
parameter_list|)
block|{
return|return
name|stem
argument_list|(
name|word
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|word
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Find the stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stems
return|;
block|}
comment|/**    * Find the unique stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|uniqueStems
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|uniqueStems
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
name|CharArraySet
name|terms
init|=
operator|new
name|CharArraySet
argument_list|(
name|dictionary
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|8
argument_list|,
name|dictionary
operator|.
name|isIgnoreCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Stem
argument_list|>
name|otherStems
init|=
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|s
range|:
name|otherStems
control|)
block|{
if|if
condition|(
operator|!
name|terms
operator|.
name|contains
argument_list|(
name|s
operator|.
name|stem
argument_list|)
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|s
operator|.
name|stem
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|stems
return|;
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Generates a list of stems for the provided word    *    * @param word Word to generate the stems for    * @param flags Flags from a previous stemming step that need to be cross-checked with any affixes in this recursive step    * @param recursionDepth Level of recursion this stemming step is at    * @return List of stems, pr an empty if no stems are found    */
DECL|method|stem
specifier|private
name|List
argument_list|<
name|Stem
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|char
index|[]
name|flags
parameter_list|,
name|int
name|recursionDepth
parameter_list|)
block|{
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|suffixes
init|=
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
name|word
argument_list|,
name|i
argument_list|,
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|HunspellAffix
name|suffix
range|:
name|suffixes
control|)
block|{
if|if
condition|(
name|hasCrossCheckedFlag
argument_list|(
name|suffix
operator|.
name|getFlag
argument_list|()
argument_list|,
name|flags
argument_list|)
condition|)
block|{
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|suffix
operator|.
name|getAppend
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// TODO: can we do this in-place?
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|append
argument_list|(
name|suffix
operator|.
name|getStrip
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Stem
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|suffix
argument_list|,
name|recursionDepth
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|stem
range|:
name|stemList
control|)
block|{
name|stem
operator|.
name|addSuffix
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|prefixes
init|=
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|HunspellAffix
name|prefix
range|:
name|prefixes
control|)
block|{
if|if
condition|(
name|hasCrossCheckedFlag
argument_list|(
name|prefix
operator|.
name|getFlag
argument_list|()
argument_list|,
name|flags
argument_list|)
condition|)
block|{
name|int
name|deAffixedStart
init|=
name|prefix
operator|.
name|getAppend
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|deAffixedStart
decl_stmt|;
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|getStrip
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|word
argument_list|,
name|deAffixedStart
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Stem
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|prefix
argument_list|,
name|recursionDepth
argument_list|)
decl_stmt|;
for|for
control|(
name|Stem
name|stem
range|:
name|stemList
control|)
block|{
name|stem
operator|.
name|addPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Applies the affix rule to the given word, producing a list of stems if any are found    *    * @param strippedWord Word the affix has been removed and the strip added    * @param affix HunspellAffix representing the affix rule itself    * @param recursionDepth Level of recursion this stemming step is at    * @return List of stems for the word, or an empty list if none are found    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applyAffix
specifier|public
name|List
argument_list|<
name|Stem
argument_list|>
name|applyAffix
parameter_list|(
name|char
name|strippedWord
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|HunspellAffix
name|affix
parameter_list|,
name|int
name|recursionDepth
parameter_list|)
block|{
if|if
condition|(
name|dictionary
operator|.
name|isIgnoreCase
argument_list|()
condition|)
block|{
name|charUtils
operator|.
name|toLowerCase
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|segment
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|segment
operator|.
name|append
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|affix
operator|.
name|checkCondition
argument_list|(
name|segment
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|List
argument_list|<
name|Stem
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|Stem
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|HunspellWord
argument_list|>
name|words
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|words
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|HunspellWord
name|hunspellWord
range|:
name|words
control|)
block|{
if|if
condition|(
name|hunspellWord
operator|.
name|hasFlag
argument_list|(
name|affix
operator|.
name|getFlag
argument_list|()
argument_list|)
condition|)
block|{
name|stems
operator|.
name|add
argument_list|(
operator|new
name|Stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|affix
operator|.
name|isCrossProduct
argument_list|()
operator|&&
name|recursionDepth
operator|<
name|RECURSION_CAP
condition|)
block|{
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
operator|.
name|getAppendFlags
argument_list|()
argument_list|,
operator|++
name|recursionDepth
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Checks if the given flag cross checks with the given array of flags    *    * @param flag Flag to cross check with the array of flags    * @param flags Array of flags to cross check against.  Can be {@code null}    * @return {@code true} if the flag is found in the array or the array is {@code null}, {@code false} otherwise    */
DECL|method|hasCrossCheckedFlag
specifier|private
name|boolean
name|hasCrossCheckedFlag
parameter_list|(
name|char
name|flag
parameter_list|,
name|char
index|[]
name|flags
parameter_list|)
block|{
return|return
name|flags
operator|==
literal|null
operator|||
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|flags
argument_list|,
name|flag
argument_list|)
operator|>=
literal|0
return|;
block|}
comment|/**    * Stem represents all information known about a stem of a word.  This includes the stem, and the prefixes and suffixes    * that were used to change the word into the stem.    */
DECL|class|Stem
specifier|public
specifier|static
class|class
name|Stem
block|{
DECL|field|prefixes
specifier|private
specifier|final
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|prefixes
init|=
operator|new
name|ArrayList
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|suffixes
specifier|private
specifier|final
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|suffixes
init|=
operator|new
name|ArrayList
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|stem
specifier|private
specifier|final
name|char
name|stem
index|[]
decl_stmt|;
DECL|field|stemLength
specifier|private
specifier|final
name|int
name|stemLength
decl_stmt|;
comment|/**      * Creates a new Stem wrapping the given word stem      *      * @param stem Stem of a word      */
DECL|method|Stem
specifier|public
name|Stem
parameter_list|(
name|char
name|stem
index|[]
parameter_list|,
name|int
name|stemLength
parameter_list|)
block|{
name|this
operator|.
name|stem
operator|=
name|stem
expr_stmt|;
name|this
operator|.
name|stemLength
operator|=
name|stemLength
expr_stmt|;
block|}
comment|/**      * Adds a prefix to the list of prefixes used to generate this stem.  Because it is assumed that prefixes are added      * depth first, the prefix is added to the front of the list      *      * @param prefix Prefix to add to the list of prefixes for this stem      */
DECL|method|addPrefix
specifier|public
name|void
name|addPrefix
parameter_list|(
name|HunspellAffix
name|prefix
parameter_list|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a suffix to the list of suffixes used to generate this stem.  Because it is assumed that suffixes are added      * depth first, the suffix is added to the end of the list      *      * @param suffix Suffix to add to the list of suffixes for this stem      */
DECL|method|addSuffix
specifier|public
name|void
name|addSuffix
parameter_list|(
name|HunspellAffix
name|suffix
parameter_list|)
block|{
name|suffixes
operator|.
name|add
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the list of prefixes used to generate the stem      *      * @return List of prefixes used to generate the stem or an empty list if no prefixes were required      */
DECL|method|getPrefixes
specifier|public
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|getPrefixes
parameter_list|()
block|{
return|return
name|prefixes
return|;
block|}
comment|/**      * Returns the list of suffixes used to generate the stem      *      * @return List of suffixes used to generate the stem or an empty list if no suffixes were required      */
DECL|method|getSuffixes
specifier|public
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|getSuffixes
parameter_list|()
block|{
return|return
name|suffixes
return|;
block|}
comment|/**      * Returns the actual word stem itself      *      * @return Word stem itself      */
DECL|method|getStem
specifier|public
name|char
index|[]
name|getStem
parameter_list|()
block|{
return|return
name|stem
return|;
block|}
comment|/**      * @return the stemLength      */
DECL|method|getStemLength
specifier|public
name|int
name|getStemLength
parameter_list|()
block|{
return|return
name|stemLength
return|;
block|}
DECL|method|getStemString
specifier|public
name|String
name|getStemString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|stem
argument_list|,
literal|0
argument_list|,
name|stemLength
argument_list|)
return|;
block|}
block|}
comment|// ================================================= Entry Point ===================================================
comment|/*    * HunspellStemmer entry point.  Accepts two arguments: location of affix file and location of dic file    *    * @param args Program arguments.  Should contain location of affix file and location of dic file    * @throws IOException Can be thrown while reading from the files    * @throws ParseException Can be thrown while parsing the files   public static void main(String[] args) throws IOException, ParseException {     boolean ignoreCase = false;     int offset = 0;          if (args.length< 2) {       System.out.println("usage: HunspellStemmer [-i]<affix location><dic location>");       System.exit(1);     }      if(args[offset].equals("-i")) {       ignoreCase = true;       System.out.println("Ignoring case. All stems will be returned lowercased");       offset++;     }          InputStream affixInputStream = new FileInputStream(args[offset++]);     InputStream dicInputStream = new FileInputStream(args[offset++]);      HunspellDictionary dictionary = new HunspellDictionary(affixInputStream, dicInputStream, Version.LUCENE_40, ignoreCase);      affixInputStream.close();     dicInputStream.close();          HunspellStemmer stemmer = new HunspellStemmer(dictionary);      Scanner scanner = new Scanner(System.in, Charset.defaultCharset().name());          System.out.print("> ");     while (scanner.hasNextLine()) {       String word = scanner.nextLine();              if ("exit".equals(word)) {         break;       }        printStemResults(word, stemmer.stem(word.toCharArray(), word.length()));              System.out.print("> ");     }   }     * Prints the results of the stemming of a word    *    * @param originalWord Word that has been stemmed    * @param stems Stems of the word   private static void printStemResults(String originalWord, List<Stem> stems) {     StringBuilder builder = new StringBuilder().append("stem(").append(originalWord).append(")").append("\n");      for (Stem stem : stems) {       builder.append("- ").append(stem.getStem()).append(": ");        for (HunspellAffix prefix : stem.getPrefixes()) {         builder.append(prefix.getAppend()).append("+");          if (hasText(prefix.getStrip())) {           builder.append(prefix.getStrip()).append("-");         }       }        builder.append(stem.getStem());        for (HunspellAffix suffix : stem.getSuffixes()) {         if (hasText(suffix.getStrip())) {           builder.append("-").append(suffix.getStrip());         }                  builder.append("+").append(suffix.getAppend());       }       builder.append("\n");     }      System.out.println(builder);   }     * Simple utility to check if the given String has any text    *    * @param str String to check if it has any text    * @return {@code true} if the String has text, {@code false} otherwise   private static boolean hasText(String str) {     return str != null&& str.length()> 0;   }   */
block|}
end_class

end_unit

