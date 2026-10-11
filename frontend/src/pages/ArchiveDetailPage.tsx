import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, ArchiveRecord } from '../api'

function ArchiveDetailPage() {
  const { docketId = '' } = useParams()
  const [archive, setArchive] = useState<ArchiveRecord | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    void (async () => {
      try {
        setError(null)
        setArchive(await api.getArchive(docketId))
      } catch (err) {
        setError((err as Error).message)
      }
    })()
  }, [docketId])

  return (
    <div className="page-shell">
      <section className="card">
        <div className="card-header">
          <div>
            <p className="eyebrow">Archive Detail</p>
            <h1>{archive?.title || docketId}</h1>
          </div>
          <Link className="secondary-button" to="/archives">Back to archives</Link>
        </div>
        {error ? <p className="error-text">{error}</p> : null}
        {archive ? (
          <div className="mini-list">
            <p className="meta-line">Archived At: {archive.archivedAt}</p>
            <p className="description">{archive.finalSummary || 'No final summary'}</p>
            <strong>Artifacts</strong>
            {archive.artifacts.length > 0 ? archive.artifacts.map((item) => <span key={item}>{item}</span>) : <span>None</span>}
            <Link className="secondary-button" to={`/dockets/${archive.docketId}`}>View source docket</Link>
          </div>
        ) : (
          !error && <p className="meta-line">Loading...</p>
        )}
      </section>
    </div>
  )
}

export default ArchiveDetailPage
