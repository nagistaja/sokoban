public interface GameListener {

    public void onMovementsUpdate (int movementsMade);

    public void onLevelEnded (int movementsMade, int levelTime);

    public void onTimerTick (int timerTick);

    public void onBoxDelivered (int boxesDelivered);

}