package bbk_beam.mtRooms.db.session;

import eadjlib.datastructure.AVLTree;

public class SessionTracker {
    AVLTree<String,SessionContainer> current_sessions = new AVLTree<>();
}
