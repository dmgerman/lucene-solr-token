begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*                     Egothor Software License version 1.00                     Copyright (C) 1997-2004 Leo Galambos.                  Copyright (C) 2002-2004 "Egothor developers"                       on behalf of the Egothor Project.                              All rights reserved.     This  software  is  copyrighted  by  the "Egothor developers". If this    license applies to a single file or document, the "Egothor developers"    are the people or entities mentioned as copyright holders in that file    or  document.  If  this  license  applies  to the Egothor project as a    whole,  the  copyright holders are the people or entities mentioned in    the  file CREDITS. This file can be found in the same location as this    license in the distribution.     Redistribution  and  use  in  source and binary forms, with or without    modification, are permitted provided that the following conditions are    met:     1. Redistributions  of  source  code  must retain the above copyright        notice, the list of contributors, this list of conditions, and the        following disclaimer.     2. Redistributions  in binary form must reproduce the above copyright        notice, the list of contributors, this list of conditions, and the        disclaimer  that  follows  these  conditions  in the documentation        and/or other materials provided with the distribution.     3. The name "Egothor" must not be used to endorse or promote products        derived  from  this software without prior written permission. For        written permission, please contact Leo.G@seznam.cz     4. Products  derived  from this software may not be called "Egothor",        nor  may  "Egothor"  appear  in  their name, without prior written        permission from Leo.G@seznam.cz.     In addition, we request that you include in the end-user documentation    provided  with  the  redistribution  and/or  in the software itself an    acknowledgement equivalent to the following:    "This product includes software developed by the Egothor Project.     http://egothor.sf.net/"     THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED    WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE    FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR    CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR    BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN    IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     This  software  consists  of  voluntary  contributions  made  by  many    individuals  on  behalf  of  the  Egothor  Project  and was originally    created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment

begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|PrintStream
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
name|List
import|;
end_import

begin_comment
comment|/**  * A Trie is used to store a dictionary of words and their stems.  *<p>  * Actually, what is stored are words with their respective patch commands. A  * trie can be termed forward (keys read from left to right) or backward (keys  * read from right to left). This property will vary depending on the language  * for which a Trie is constructed.  */
end_comment

begin_class
DECL|class|Trie
specifier|public
class|class
name|Trie
block|{
DECL|field|rows
name|List
argument_list|<
name|Row
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|cmds
name|List
argument_list|<
name|CharSequence
argument_list|>
name|cmds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|root
name|int
name|root
decl_stmt|;
DECL|field|forward
name|boolean
name|forward
init|=
literal|false
decl_stmt|;
comment|/**    * Constructor for the Trie object.    *     * @param is the input stream    * @exception IOException if an I/O error occurs    */
DECL|method|Trie
specifier|public
name|Trie
parameter_list|(
name|DataInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|forward
operator|=
name|is
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|root
operator|=
name|is
operator|.
name|readInt
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|is
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|cmds
operator|.
name|add
argument_list|(
name|is
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|is
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Row
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Constructor for the Trie object.    *     * @param forward set to<tt>true</tt>    */
DECL|method|Trie
specifier|public
name|Trie
parameter_list|(
name|boolean
name|forward
parameter_list|)
block|{
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Row
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|forward
operator|=
name|forward
expr_stmt|;
block|}
comment|/**    * Constructor for the Trie object.    *     * @param forward<tt>true</tt> if read left to right,<tt>false</tt> if read    *          right to left    * @param root index of the row that is the root node    * @param cmds the patch commands to store    * @param rows a Vector of Vectors. Each inner Vector is a node of this Trie    */
DECL|method|Trie
specifier|public
name|Trie
parameter_list|(
name|boolean
name|forward
parameter_list|,
name|int
name|root
parameter_list|,
name|List
argument_list|<
name|CharSequence
argument_list|>
name|cmds
parameter_list|,
name|List
argument_list|<
name|Row
argument_list|>
name|rows
parameter_list|)
block|{
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
name|this
operator|.
name|cmds
operator|=
name|cmds
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|forward
operator|=
name|forward
expr_stmt|;
block|}
comment|/**    * Gets the all attribute of the Trie object    *     * @param key Description of the Parameter    * @return The all value    */
DECL|method|getAll
specifier|public
name|CharSequence
index|[]
name|getAll
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|int
name|res
index|[]
init|=
operator|new
name|int
index|[
name|key
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|int
name|resc
init|=
literal|0
decl_stmt|;
name|Row
name|now
init|=
name|getRow
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|int
name|w
decl_stmt|;
name|StrEnum
name|e
init|=
operator|new
name|StrEnum
argument_list|(
name|key
argument_list|,
name|forward
argument_list|)
decl_stmt|;
name|boolean
name|br
init|=
literal|false
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
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|Character
name|ch
init|=
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|=
name|now
operator|.
name|getCmd
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|int
name|n
init|=
name|w
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|resc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|n
operator|==
name|res
index|[
name|j
index|]
condition|)
block|{
name|n
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|n
operator|>=
literal|0
condition|)
block|{
name|res
index|[
name|resc
operator|++
index|]
operator|=
name|n
expr_stmt|;
block|}
block|}
name|w
operator|=
name|now
operator|.
name|getRef
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|now
operator|=
name|getRow
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|br
operator|==
literal|false
condition|)
block|{
name|w
operator|=
name|now
operator|.
name|getCmd
argument_list|(
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|int
name|n
init|=
name|w
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|resc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|n
operator|==
name|res
index|[
name|j
index|]
condition|)
block|{
name|n
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|n
operator|>=
literal|0
condition|)
block|{
name|res
index|[
name|resc
operator|++
index|]
operator|=
name|n
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|resc
operator|<
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CharSequence
name|R
index|[]
init|=
operator|new
name|CharSequence
index|[
name|resc
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|resc
condition|;
name|j
operator|++
control|)
block|{
name|R
index|[
name|j
index|]
operator|=
name|cmds
operator|.
name|get
argument_list|(
name|res
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|R
return|;
block|}
comment|/**    * Return the number of cells in this Trie object.    *     * @return the number of cells    */
DECL|method|getCells
specifier|public
name|int
name|getCells
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Row
name|row
range|:
name|rows
control|)
name|size
operator|+=
name|row
operator|.
name|getCells
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/**    * Gets the cellsPnt attribute of the Trie object    *     * @return The cellsPnt value    */
DECL|method|getCellsPnt
specifier|public
name|int
name|getCellsPnt
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Row
name|row
range|:
name|rows
control|)
name|size
operator|+=
name|row
operator|.
name|getCellsPnt
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/**    * Gets the cellsVal attribute of the Trie object    *     * @return The cellsVal value    */
DECL|method|getCellsVal
specifier|public
name|int
name|getCellsVal
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Row
name|row
range|:
name|rows
control|)
name|size
operator|+=
name|row
operator|.
name|getCellsVal
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/**    * Return the element that is stored in a cell associated with the given key.    *     * @param key the key    * @return the associated element    */
DECL|method|getFully
specifier|public
name|CharSequence
name|getFully
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|Row
name|now
init|=
name|getRow
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|int
name|w
decl_stmt|;
name|Cell
name|c
decl_stmt|;
name|int
name|cmd
init|=
operator|-
literal|1
decl_stmt|;
name|StrEnum
name|e
init|=
operator|new
name|StrEnum
argument_list|(
name|key
argument_list|,
name|forward
argument_list|)
decl_stmt|;
name|Character
name|ch
init|=
literal|null
decl_stmt|;
name|Character
name|aux
init|=
literal|null
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
name|key
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
name|ch
operator|=
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|c
operator|=
name|now
operator|.
name|at
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|cmd
operator|=
name|c
operator|.
name|cmd
expr_stmt|;
for|for
control|(
name|int
name|skip
init|=
name|c
operator|.
name|skip
init|;
name|skip
operator|>
literal|0
condition|;
name|skip
operator|--
control|)
block|{
if|if
condition|(
name|i
operator|<
name|key
operator|.
name|length
argument_list|()
condition|)
block|{
name|aux
operator|=
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
name|i
operator|++
expr_stmt|;
block|}
name|w
operator|=
name|now
operator|.
name|getRef
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|now
operator|=
name|getRow
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
name|key
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|(
name|cmd
operator|==
operator|-
literal|1
operator|)
condition|?
literal|null
else|:
name|cmds
operator|.
name|get
argument_list|(
name|cmd
argument_list|)
return|;
block|}
comment|/**    * Return the element that is stored as last on a path associated with the    * given key.    *     * @param key the key associated with the desired element    * @return the last on path element    */
DECL|method|getLastOnPath
specifier|public
name|CharSequence
name|getLastOnPath
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|Row
name|now
init|=
name|getRow
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|int
name|w
decl_stmt|;
name|CharSequence
name|last
init|=
literal|null
decl_stmt|;
name|StrEnum
name|e
init|=
operator|new
name|StrEnum
argument_list|(
name|key
argument_list|,
name|forward
argument_list|)
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
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|Character
name|ch
init|=
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|=
name|now
operator|.
name|getCmd
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|last
operator|=
name|cmds
operator|.
name|get
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
name|w
operator|=
name|now
operator|.
name|getRef
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|w
operator|>=
literal|0
condition|)
block|{
name|now
operator|=
name|getRow
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|last
return|;
block|}
block|}
name|w
operator|=
name|now
operator|.
name|getCmd
argument_list|(
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|w
operator|>=
literal|0
operator|)
condition|?
name|cmds
operator|.
name|get
argument_list|(
name|w
argument_list|)
else|:
name|last
return|;
block|}
comment|/**    * Return the Row at the given index.    *     * @param index the index containing the desired Row    * @return the Row    */
DECL|method|getRow
specifier|private
name|Row
name|getRow
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|rows
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|rows
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Write this Trie to the given output stream.    *     * @param os the output stream    * @exception IOException if an I/O error occurs    */
DECL|method|store
specifier|public
name|void
name|store
parameter_list|(
name|DataOutput
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|writeBoolean
argument_list|(
name|forward
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|cmds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CharSequence
name|cmd
range|:
name|cmds
control|)
name|os
operator|.
name|writeUTF
argument_list|(
name|cmd
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Row
name|row
range|:
name|rows
control|)
name|row
operator|.
name|store
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the given key associated with the given patch command. If either    * parameter is null this method will return without executing.    *     * @param key the key    * @param cmd the patch command    */
DECL|method|add
name|void
name|add
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|CharSequence
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
operator|||
name|cmd
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|cmd
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|id_cmd
init|=
name|cmds
operator|.
name|indexOf
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|id_cmd
operator|==
operator|-
literal|1
condition|)
block|{
name|id_cmd
operator|=
name|cmds
operator|.
name|size
argument_list|()
expr_stmt|;
name|cmds
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|int
name|node
init|=
name|root
decl_stmt|;
name|Row
name|r
init|=
name|getRow
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|StrEnum
name|e
init|=
operator|new
name|StrEnum
argument_list|(
name|key
argument_list|,
name|forward
argument_list|)
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
name|e
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|Character
name|ch
init|=
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|=
name|r
operator|.
name|getRef
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|>=
literal|0
condition|)
block|{
name|r
operator|=
name|getRow
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
name|rows
operator|.
name|size
argument_list|()
expr_stmt|;
name|Row
name|n
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|n
operator|=
operator|new
name|Row
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|setRef
argument_list|(
name|ch
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|r
operator|=
name|n
expr_stmt|;
block|}
block|}
name|r
operator|.
name|setCmd
argument_list|(
operator|new
name|Character
argument_list|(
name|e
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|id_cmd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove empty rows from the given Trie and return the newly reduced Trie.    *     * @param by the Trie to reduce    * @return the newly reduced Trie    */
DECL|method|reduce
specifier|public
name|Trie
name|reduce
parameter_list|(
name|Reduce
name|by
parameter_list|)
block|{
return|return
name|by
operator|.
name|optimize
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** writes debugging info to the printstream */
DECL|method|printInfo
specifier|public
name|void
name|printInfo
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|CharSequence
name|prefix
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
literal|"nds "
operator|+
name|rows
operator|.
name|size
argument_list|()
operator|+
literal|" cmds "
operator|+
name|cmds
operator|.
name|size
argument_list|()
operator|+
literal|" cells "
operator|+
name|getCells
argument_list|()
operator|+
literal|" valcells "
operator|+
name|getCellsVal
argument_list|()
operator|+
literal|" pntcells "
operator|+
name|getCellsPnt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class is part of the Egothor Project    */
DECL|class|StrEnum
class|class
name|StrEnum
block|{
DECL|field|s
name|CharSequence
name|s
decl_stmt|;
DECL|field|from
name|int
name|from
decl_stmt|;
DECL|field|by
name|int
name|by
decl_stmt|;
comment|/**      * Constructor for the StrEnum object      *       * @param s Description of the Parameter      * @param up Description of the Parameter      */
DECL|method|StrEnum
name|StrEnum
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|boolean
name|up
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
if|if
condition|(
name|up
condition|)
block|{
name|from
operator|=
literal|0
expr_stmt|;
name|by
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|from
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
expr_stmt|;
name|by
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|length
name|int
name|length
parameter_list|()
block|{
return|return
name|s
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|next
name|char
name|next
parameter_list|()
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|from
operator|+=
name|by
expr_stmt|;
return|return
name|ch
return|;
block|}
block|}
block|}
end_class

end_unit

