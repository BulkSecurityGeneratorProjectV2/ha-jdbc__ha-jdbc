/*
 * HA-JDBC: High-Availability JDBC
 * Copyright 2004-2009 Paul Ferraro
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.hajdbc.durability.fine;

import net.sf.hajdbc.Database;
import net.sf.hajdbc.ExceptionFactory;
import net.sf.hajdbc.durability.DurabilityListener;
import net.sf.hajdbc.durability.InvokerEvent;
import net.sf.hajdbc.durability.TransactionIdentifier;
import net.sf.hajdbc.durability.coarse.CoarseDurability;
import net.sf.hajdbc.sql.Invoker;

/**
 * @author paul
 *
 */
public class FineDurability<Z, D extends Database<Z>> extends CoarseDurability<Z, D>
{
	final DurabilityListener listener;
	
	public FineDurability(DurabilityListener listener)
	{
		super(listener);
		
		this.listener = listener;
	}

	/**
	 * {@inheritDoc}
	 * @see net.sf.hajdbc.durability.Durability#getInvoker(net.sf.hajdbc.sql.Invoker)
	 */
	@Override
	public <T, R, E extends Exception> Invoker<Z, D, T, R, E> getInvoker(final Invoker<Z, D, T, R, E> invoker, final Phase phase, final TransactionIdentifier transactionId, final ExceptionFactory<E> exceptionFactory)
	{
		return new Invoker<Z, D, T, R, E>()
		{
			@Override
			public R invoke(D database, T object) throws E
			{
				InvokerEvent event = new InvokerEvent(transactionId, phase, database);
				
				FineDurability.this.listener.beforeInvoker(event);
				
				try
				{
					R result = invoker.invoke(database, object);
					
					event.setResult(result);
					
					return result;
				}
				catch (Exception e)
				{
					E exception = exceptionFactory.createException(e);

					event.setResult(exception);
					
					throw exception;
				}
				finally
				{
					FineDurability.this.listener.afterInvoker(event);
				}
			}
		};
	}
}