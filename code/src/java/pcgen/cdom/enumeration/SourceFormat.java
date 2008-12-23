package pcgen.cdom.enumeration;

import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;

public enum SourceFormat
{

	SHORT
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			return cdo.get(StringKey.SOURCE_SHORT);
		}

		@Override
		public StringKey getPublisherKey()
		{
			return null;
		}

		@Override
		public boolean allowsPage()
		{
			return true;
		}
	},

	MEDIUM
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			return cdo.get(StringKey.SOURCE_LONG);
		}

		@Override
		public StringKey getPublisherKey()
		{
			return null;
		}

		@Override
		public boolean allowsPage()
		{
			return true;
		}
	},

	LONG
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			return cdo.get(StringKey.SOURCE_LONG);
		}

		@Override
		public StringKey getPublisherKey()
		{
			return StringKey.PUB_NAME_LONG;
		}

		@Override
		public boolean allowsPage()
		{
			return true;
		}
	},

	DATE
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			Date date = cdo.get(ObjectKey.SOURCE_DATE);
			return date == null ? null : date.toString();
		}

		@Override
		public StringKey getPublisherKey()
		{
			return null;
		}

		@Override
		public boolean allowsPage()
		{
			return true;
		}
	},

	PAGE
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			return cdo.get(StringKey.SOURCE_PAGE);
		}

		@Override
		public StringKey getPublisherKey()
		{
			return null;
		}

		@Override
		public boolean allowsPage()
		{
			return true;
		}
	},

	WEB
	{
		@Override
		public String getField(CDOMObject cdo)
		{
			return cdo.get(StringKey.SOURCE_WEB);
		}

		@Override
		public StringKey getPublisherKey()
		{
			return StringKey.PUB_NAME_WEB;
		}

		@Override
		public boolean allowsPage()
		{
			return false;
		}
	};

	public abstract StringKey getPublisherKey();
	
	public abstract String getField(CDOMObject cdo);

	/**
	 * Does this format allow page information?
	 * 
	 * <p>
	 * If a format does not allow page information then page information will
	 * not be included in the formatted output even if it is requested. This is
	 * used primarily to prevent silly combinations like website, page number.
	 * 
	 * @return <tt>true</tt> if the page information can be included.
	 */
	public abstract boolean allowsPage();

	public static String formatShort(CDOMObject cdo, int aMaxLen)
	{
		String theShortName = cdo.get(StringKey.SOURCE_SHORT);
		if (theShortName == null)
		{
			// if this item's source is null, try to get it from theCampaign
			Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
			if (campaign != null)
			{
				theShortName = campaign.get(StringKey.SOURCE_SHORT);
			}
		}
		if (theShortName != null)
		{
			final int maxLen = Math.min(aMaxLen, theShortName.length());
			return theShortName.substring(0, maxLen);
		}
		return Constants.EMPTY_STRING;
	}

	/**
	 * Returns a formatted string representation for this source based on the
	 * <tt>SourceFormat</tt> passed in.
	 * 
	 * @param aFormat
	 *            The format to display the source in
	 * @param includePage
	 *            Should the page number be included in the output
	 * 
	 * @return A formatted string.
	 * 
	 * @see pcgen.core.SourceEntry.SourceFormat
	 */
	public static String getFormattedString(CDOMObject cdo,
			SourceFormat format, boolean includePage)
	{
		StringBuffer ret = new StringBuffer();
		String source = format.getField(cdo);

		String publisher = null;
		Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
		if (campaign != null)
		{
			// If sourceCampaign object exists, get it's publisher entry for
			// the same key
			StringKey pubkey = format.getPublisherKey();
			if (pubkey != null)
			{
				String pub = campaign.get(pubkey);
				publisher = pub == null ? "" : pub;
			}

			// if this item's source is null, try to get it from theCampaign
			if (source == null)
			{
				source = format.getField(campaign);
			}
		}
		if (source == null)
		{
			source = Constants.EMPTY_STRING;
		}

		if (publisher != null && publisher.trim().length() > 0)
		{
			ret.append(publisher);
			ret.append(" - "); //$NON-NLS-1$
		}
		ret.append(source);

		if (includePage && format.allowsPage())
		{
			String thePageNumber = cdo.get(StringKey.SOURCE_PAGE);
			if (thePageNumber != null)
			{
				if (ret.length() != 0)
				{
					ret.append(", "); //$NON-NLS-1$
				}
				ret.append(thePageNumber);
			}

		}
		return ret.toString();
	}

}
