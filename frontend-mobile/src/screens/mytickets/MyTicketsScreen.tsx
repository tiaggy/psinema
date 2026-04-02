import { View, Text, FlatList, TouchableOpacity, StyleSheet, ActivityIndicator } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getMyTickets } from '../../api/orders';
import { useAuthStore } from '../../store/authStore';

const STATUS_COLOR: Record<string, string> = { ACTIVE: '#2ecc71', USED: '#aaa', CANCELLED: '#e74c3c' };

export default function MyTicketsScreen({ navigation }: any) {
  const { isAuthenticated } = useAuthStore();
  const { data: tickets, isLoading } = useQuery({
    queryKey: ['myTickets'],
    queryFn: getMyTickets,
    enabled: isAuthenticated(),
  });

  if (!isAuthenticated()) {
    return (
      <View style={styles.center}>
        <Text style={styles.empty}>Sign in to view your tickets.</Text>
        <TouchableOpacity style={styles.btn} onPress={() => navigation.navigate('Account')}>
          <Text style={styles.btnText}>Go to Account</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>My Tickets</Text>
      {isLoading ? <ActivityIndicator color="#e94560" style={{ marginTop: 40 }} /> : (
        <FlatList
          data={tickets}
          keyExtractor={t => String(t.id)}
          contentContainerStyle={{ paddingBottom: 20 }}
          renderItem={({ item: t }) => (
            <TouchableOpacity style={styles.card} onPress={() => navigation.navigate('TicketDetail', { ticket: t })}>
              <View style={styles.cardLeft}>
                <Text style={styles.movieTitle}>{t.order.screening.movie.title}</Text>
                <Text style={styles.meta}>{new Date(t.order.screening.startTime).toLocaleString()}</Text>
                <Text style={styles.meta}>{t.order.screening.hall.name} · Row {t.seat.rowNumber}, Seat {t.seat.seatNumber}</Text>
                <Text style={styles.type}>{t.type}</Text>
              </View>
              <View>
                <View style={[styles.statusBadge, { backgroundColor: STATUS_COLOR[t.status] }]}>
                  <Text style={styles.statusText}>{t.status}</Text>
                </View>
              </View>
            </TouchableOpacity>
          )}
          ListEmptyComponent={<Text style={styles.empty}>No tickets found.</Text>}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 16 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32 },
  heading: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', marginBottom: 16 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 14, marginBottom: 10, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', elevation: 1 },
  cardLeft: { flex: 1 },
  movieTitle: { fontSize: 15, fontWeight: '600', color: '#1a1a2e', marginBottom: 3 },
  meta: { fontSize: 12, color: '#888' },
  type: { fontSize: 11, color: '#e94560', marginTop: 4 },
  statusBadge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12 },
  statusText: { fontSize: 11, color: '#fff', fontWeight: '600' },
  empty: { color: '#999', textAlign: 'center', fontSize: 14, marginBottom: 16 },
  btn: { backgroundColor: '#e94560', borderRadius: 8, paddingHorizontal: 24, paddingVertical: 12 },
  btnText: { color: '#fff', fontWeight: '600' },
});
