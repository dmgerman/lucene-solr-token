begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
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

begin_interface
DECL|interface|InputStreamObserver
specifier|public
interface|interface
name|InputStreamObserver
block|{
DECL|method|notifyOpened
specifier|public
name|void
name|notifyOpened
parameter_list|(
name|ObservableInputStream
name|in
parameter_list|,
name|long
name|timeElapsed
parameter_list|)
function_decl|;
DECL|method|notifyClosed
specifier|public
name|void
name|notifyClosed
parameter_list|(
name|ObservableInputStream
name|in
parameter_list|,
name|long
name|timeElapsed
parameter_list|)
function_decl|;
DECL|method|notifyRead
specifier|public
name|void
name|notifyRead
parameter_list|(
name|ObservableInputStream
name|in
parameter_list|,
name|long
name|timeElapsed
parameter_list|,
name|int
name|nrRead
parameter_list|,
name|int
name|totalRead
parameter_list|)
function_decl|;
DECL|method|notifyFinished
specifier|public
name|void
name|notifyFinished
parameter_list|(
name|ObservableInputStream
name|in
parameter_list|,
name|long
name|timeElapsed
parameter_list|,
name|int
name|totalRead
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

