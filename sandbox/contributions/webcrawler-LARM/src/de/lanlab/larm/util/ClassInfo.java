begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  prints class information with the reflection api  *  for debugging only  */
end_comment

begin_class
DECL|class|ClassInfo
specifier|public
class|class
name|ClassInfo
block|{
DECL|method|ClassInfo
specifier|public
name|ClassInfo
parameter_list|()
block|{     }
comment|/**      * Usage: java ClassInfo PackageName.MyNewClassName PackageName.DerivedClassName      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|name
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|derivedName
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|LinkedList
name|l
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
name|ListIterator
name|itry
init|=
name|l
operator|.
name|listIterator
argument_list|()
decl_stmt|;
try|try
block|{
name|Class
name|cls
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|name
operator|=
name|cls
operator|.
name|getName
argument_list|()
expr_stmt|;
name|String
name|pkg
init|=
name|getPackageName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|clss
init|=
name|getClassName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|StringWriter
name|importsWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|imports
init|=
operator|new
name|PrintWriter
argument_list|(
name|importsWriter
argument_list|)
decl_stmt|;
name|StringWriter
name|outWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|outWriter
argument_list|)
decl_stmt|;
name|TreeSet
name|importClasses
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
name|importClasses
operator|.
name|add
argument_list|(
name|getImportStatement
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/**\n * (class description here)\n */\npublic class "
operator|+
name|derivedName
operator|+
literal|" "
operator|+
operator|(
name|cls
operator|.
name|isInterface
argument_list|()
condition|?
literal|"implements "
else|:
literal|"extends "
operator|)
operator|+
name|clss
operator|+
literal|"\n{"
argument_list|)
expr_stmt|;
name|Method
index|[]
name|m
init|=
name|cls
operator|.
name|getMethods
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
name|m
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|thism
init|=
name|m
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|thism
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|PRIVATE
operator|)
operator|==
literal|0
operator|&&
operator|(
operator|(
name|thism
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|FINAL
operator|)
operator|==
literal|0
operator|)
operator|&&
operator|(
name|thism
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|!=
literal|"java.lang.Object"
operator|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"    /**"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"     * (method description here)"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"     * defined in "
operator|+
name|thism
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Class
index|[]
name|parameters
init|=
name|thism
operator|.
name|getParameterTypes
argument_list|()
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
name|parameters
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|getPackageName
argument_list|(
name|parameters
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
operator|!=
literal|""
condition|)
block|{
name|importClasses
operator|.
name|add
argument_list|(
name|getImportStatement
argument_list|(
name|parameters
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"     * @param p"
operator|+
name|j
operator|+
literal|" (parameter description here)"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|thism
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
operator|!=
literal|"void"
condition|)
block|{
name|String
name|returnPackage
init|=
name|getPackageName
argument_list|(
name|thism
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnPackage
operator|!=
literal|""
condition|)
block|{
name|importClasses
operator|.
name|add
argument_list|(
name|getImportStatement
argument_list|(
name|thism
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"     * @return (return value description here)"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"     */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"    "
operator|+
name|getModifierString
argument_list|(
name|thism
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|+
name|getClassName
argument_list|(
name|thism
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|thism
operator|.
name|getName
argument_list|()
operator|+
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|parameters
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|getClassName
argument_list|(
name|parameters
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
literal|" p"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|Class
index|[]
name|exceptions
init|=
name|thism
operator|.
name|getExceptionTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|exceptions
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" throws "
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|exceptions
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|k
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|String
name|exCompleteName
init|=
name|exceptions
index|[
name|k
index|]
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|exName
init|=
name|getClassName
argument_list|(
name|exCompleteName
argument_list|)
decl_stmt|;
name|importClasses
operator|.
name|add
argument_list|(
name|getImportStatement
argument_list|(
name|exCompleteName
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|exName
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"\n"
operator|+
literal|"    {\n"
operator|+
literal|"        /**@todo: Implement this "
operator|+
name|thism
operator|.
name|getName
argument_list|()
operator|+
literal|"() method */\n"
operator|+
literal|"        throw new UnsupportedOperationException(\"Method "
operator|+
name|thism
operator|.
name|getName
argument_list|()
operator|+
literal|"() not yet implemented.\");\n"
operator|+
literal|"    }\n\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|Iterator
name|importIterator
init|=
name|importClasses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|importIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|importName
init|=
operator|(
name|String
operator|)
name|importIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|importName
operator|.
name|startsWith
argument_list|(
literal|"java.lang"
argument_list|)
condition|)
block|{
name|imports
operator|.
name|println
argument_list|(
literal|"import "
operator|+
name|importName
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|imports
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|getPackageName
argument_list|(
name|derivedName
argument_list|)
operator|!=
literal|""
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"package "
operator|+
name|getPackageName
argument_list|(
name|derivedName
argument_list|)
operator|+
literal|";\n"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"/**\n"
operator|+
literal|" * Title:        \n"
operator|+
literal|" * Description:\n"
operator|+
literal|" * Copyright:    Copyright (c)\n"
operator|+
literal|" * Company:\n"
operator|+
literal|" * @author\n"
operator|+
literal|" * @version 1.0\n"
operator|+
literal|" */\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|importsWriter
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|outWriter
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPackageName
specifier|public
specifier|static
name|String
name|getPackageName
parameter_list|(
name|String
name|className
parameter_list|)
block|{
if|if
condition|(
name|className
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'['
condition|)
block|{
switch|switch
condition|(
name|className
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
literal|'L'
case|:
return|return
name|getPackageName
argument_list|(
name|className
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|className
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
default|default:
return|return
literal|""
return|;
block|}
block|}
name|String
name|name
init|=
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|!=
operator|-
literal|1
condition|?
name|className
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
argument_list|)
else|:
literal|""
decl_stmt|;
comment|//System.out.println("Package: " + name);
return|return
name|name
return|;
block|}
DECL|method|getClassName
specifier|public
specifier|static
name|String
name|getClassName
parameter_list|(
name|String
name|className
parameter_list|)
block|{
if|if
condition|(
name|className
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'['
condition|)
block|{
switch|switch
condition|(
name|className
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
literal|'L'
case|:
return|return
name|getClassName
argument_list|(
name|className
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|className
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|+
literal|"[]"
return|;
case|case
literal|'C'
case|:
return|return
literal|"char[]"
return|;
case|case
literal|'I'
case|:
return|return
literal|"int[]"
return|;
case|case
literal|'B'
case|:
return|return
literal|"byte[]"
return|;
comment|// rest is missing here
block|}
block|}
name|String
name|name
init|=
operator|(
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|>
operator|-
literal|1
operator|)
condition|?
name|className
operator|.
name|substring
argument_list|(
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|)
else|:
name|className
decl_stmt|;
comment|//System.out.println("Class: "  + name);
return|return
name|name
return|;
block|}
DECL|method|getImportStatement
specifier|static
name|String
name|getImportStatement
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|String
name|pack
init|=
name|getPackageName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|String
name|clss
init|=
name|getClassName
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|clss
operator|.
name|indexOf
argument_list|(
literal|"[]"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
return|return
name|pack
operator|+
literal|"."
operator|+
name|clss
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clss
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|pack
operator|+
literal|"."
operator|+
name|clss
return|;
block|}
block|}
DECL|method|getModifierString
specifier|public
specifier|static
name|String
name|getModifierString
parameter_list|(
name|int
name|modifiers
parameter_list|)
block|{
name|StringBuffer
name|mods
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|ABSTRACT
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"abstract "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|FINAL
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"final "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|INTERFACE
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"interface "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|NATIVE
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"native "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|PRIVATE
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"private "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|PROTECTED
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"protected "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|PUBLIC
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"public "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"static "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|STRICT
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"strictfp "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|SYNCHRONIZED
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"synchronized "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|TRANSIENT
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"transient "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|modifiers
operator|&
name|Modifier
operator|.
name|VOLATILE
operator|)
operator|!=
literal|0
condition|)
block|{
name|mods
operator|.
name|append
argument_list|(
literal|"volatile "
argument_list|)
expr_stmt|;
block|}
return|return
name|mods
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

