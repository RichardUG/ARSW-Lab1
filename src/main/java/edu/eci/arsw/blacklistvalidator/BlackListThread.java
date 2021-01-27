package edu.eci.arsw.blacklistvalidator;
import java.util.LinkedList;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
public class BlackListThread extends Thread{
    private static final int BLACK_LIST_ALARM_COUNT=5;
	private int inicio;
	private int fin;
	private String Host;
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    private int checkedListsCount;
    private int ocurrencesCount;
	public BlackListThread(int i,int f,String Host) { 
		this.inicio=i;
		this.fin=f;
		this.Host=Host;
		this.checkedListsCount = 0;
		this.ocurrencesCount = 0;
	}
	public void run() {

        for (int i=inicio;i<fin && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, Host)) {
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        }
        
	}
	public LinkedList<Integer> getBlackListOcurrences() {
		return blackListOcurrences;
	}
	public int getCheckedListsCount() {
		return checkedListsCount;
	}
	public int getOcurrencesCount() {
		return ocurrencesCount;
	}
}